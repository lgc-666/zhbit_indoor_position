package zhbit.za102;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.x.protobuf.MysqlxExpr;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import redis.clients.jedis.Jedis;
import zhbit.za102.Utils.DataUtil;
import zhbit.za102.Utils.RedisUtils;
import zhbit.za102.Utils.SerializeUtil;
import zhbit.za102.bean.Class;
import zhbit.za102.bean.ClassData;
import zhbit.za102.bean.Device;
import zhbit.za102.bean.MacSortByDate;
import zhbit.za102.dao.*;
import zhbit.za102.service.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DataProcessor implements CommandLineRunner {
    //监听器端口（全局常量）
    private final static int PORT = 3021;
    private DatagramSocket ds;
    private DatagramPacket dp;
    private byte[] buf = null;
    private String strReceive;

    //解析json数据
    private JSONObject jsonObject = null;
    private JSONObject jsonObjectData = null;
    private JSONArray jsonArray = null;

    //正则表达式筛选数据
    private String macM = "([a-f0-9A-F]{2}:){5}[a-f0-9A-F]{2}";
    private String rssiM = "-[1-9]\\d*";
    private String idM = "^[1-9]\\d*$";

    //数据处理
    private String indoorname;
    private String machineId;
    private String mac;
    private Integer rssi;
    private String data;  //发送过来的信息中所收到的data属性值
    private Integer dataSize; //有几个mac行
    private Map<String, Object> macMap;
    private Integer timeCount;//计算离开时间
    private Timestamp latest_time;//当前时间戳
    private Integer visited_times;//访问次数
    private Integer StopJudege;  //所在区域是否为禁止区域
    private String atAddress;  //所在区域

    //AP数量
    private Integer APcount = 0;

    //新客人
    private Integer new_student = 0;
    //进入区域人数
    private Integer in_class_number = 0;
    //当前区域人数
    private Integer class_now_number = 0;
    //小时进入区域数量
    private Integer hour_in_class_number = 0;

    private String lastmachineid="";
    private String nowmachineid="";
    private String lastindoorname="";
    private String nowindoorname="";
    private String lastx;
    private String lasty;
    private String nowx;
    private String nowy;

    @Resource
    RedisUtils redisUtil;
    @Autowired
    DataUtil dataUtil;
    @Autowired
    ClassDataMapper classDataMapper;
    @Autowired
    VisitMapper visitMapper;
    @Autowired
    StopVisitMapper stopVisitMapper;
    @Autowired
    MachineMapper machineMapper;
    @Autowired
    LocationMapper locationMapper;

    @Autowired
    DeviceService deviceService;
    @Autowired
    LogrecordService logrecordService;
    @Autowired
    ClassDataService classDataService;
    @Autowired
    ClassService classService;
    @Autowired
    MachineService machineService;

    @Override
    public void run(String... args) throws Exception {
        try {
            Socket1 s1=new Socket1();
            Socket2 s2=new Socket2();
            Thread t1=new Thread(s1);
            Thread t2=new Thread(s2);
            t1.start();
            t2.start();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //ds.close();
            System.out.println(".................线程已启动.................");
        }
    }

    //此进程用于存储跳出量、动态当前客流量和小时客流量
    @Transactional
    @Async
    @Scheduled(cron = "0/3 * * * * ?")
    public void dataThread() {  //3秒一次，异步执行进程，因为切换快给人感觉像同时进行一样
        //总小时客流量
        Integer subHour_customer_sum = 0;
        //小时客流量
        Integer subHour_customer = 0;
        //现存人数
        Integer dynamic_customer = 0;
        //跳出量
        Integer jumpOut_customer = 0;
        //小时客流量
        Integer subHour_customer2 = 0;
        //现存人数
        Integer dynamic_customer2 = 0;
        //跳出量
        Integer jumpOut_customer2 = 0;

        String subAddress = null;
        String subIndoorname = null;
        String subAddress2 = null;
        String subIndoorname2 = null;

        Map<String, Object> countExtraMap = new HashMap<>();
        Map<String, Integer> subCountExtraMap;
        Map<String, Object> subCustomerMap = redisUtil.hmget("visit");
        Map<String, Object> countExtraMap2 = new HashMap<>();
        Map<String, Integer> subCountExtraMap2;
        Map<String, Object> subCustomerMap2 = redisUtil.hmget("stopvisit");

        List<Class> a = classService.list();
        //先算总的后面遍历再减去自己的
        for (Class b : a) {
            //当前小时进店量总和
            if(classDataMapper.searchNowHour_in_customer_number(b.getAdress(), b.getIndoorname())!=null){
                subHour_customer_sum = subHour_customer_sum + classDataMapper.searchNowHour_in_customer_number(b.getAdress(), b.getIndoorname());
            }
        }

        if (subCustomerMap != null) {
                latest_time = new Timestamp(System.currentTimeMillis());
                //普通区域
                for (Map.Entry<String, Object> subCustomerMap_1 : subCustomerMap.entrySet()) {  //subCustomerMap为多个键值对
                    Object subMac = subCustomerMap_1.getKey();  //获得键mac+'-'+address+'-'+indoorname
                    Map<String, Object> subCustomerMap_2 = (Map) redisUtil.hget("visit", (String) subMac);
                    //时间间隔
                    Integer countTime = new Long((latest_time.getTime() - (Long) subCustomerMap_2.get("beat")) / (60 * 1000)).intValue();
                    //大于1分钟没有心跳的店内客人
                    if (countTime >= 1 && (Integer) subCustomerMap_2.get("inJudge") == 1) {
                        Long stayTime = ((Long) subCustomerMap_2.get("left_time") - (Long) subCustomerMap_2.get("in_time")) / 1000;
                        //if (stayTime < 50)  //离开时间（最后一次在店时间）-上次进店小于50秒（进来不到1分钟就走了）
                        //{
                        jumpOut_customer = 1;  //跳出量+1
                        //}
                        subCustomerMap_2.put("inJudge", 0); //不在区域内
                        visitMapper.updateInjudge2(0, subCustomerMap_2.get("mac").toString(), subCustomerMap_2.get("address").toString(), subCustomerMap_2.get("indoorname").toString());
                        subCustomerMap_2.put("rt", stayTime.toString()); //停留时间
                    } else if (countTime < 1 && (Integer) subCustomerMap_2.get("inJudge") == 1) { //人在室内
                        dynamic_customer = 1; //现存人数+1
                    }
                    subAddress = (String) subCustomerMap_2.get("address");
                    subIndoorname = (String) subCustomerMap_2.get("indoorname");
                    if (jumpOut_customer != 0 || dynamic_customer != 0) {
                        if (countExtraMap.get(subAddress) == null) {
                            subCountExtraMap = new HashMap<>();
                            subCountExtraMap.put("jumpOut_customer", jumpOut_customer);
                            subCountExtraMap.put("dynamic_customer", dynamic_customer);
                            countExtraMap.put(subAddress, subCountExtraMap);
                        } else {
                            subCountExtraMap = (Map) countExtraMap.get(subAddress);
                            subCountExtraMap.put("jumpOut_customer", subCountExtraMap.get("jumpOut_customer") + jumpOut_customer);
                            subCountExtraMap.put("dynamic_customer", subCountExtraMap.get("dynamic_customer") + dynamic_customer);
                            countExtraMap.put(subAddress, subCountExtraMap);
                        }
                    }
                    jumpOut_customer = 0;
                    dynamic_customer = 0;
                    //更新缓存（主要更新inJudge值）
                    redisUtil.hset("visit", subMac.toString(), subCustomerMap_2);
                }

                System.out.println("步骤1");
                //存到数据库中（遍历上面存储跳出量的map）
                for (Map.Entry<String, Object> subCustomerMap_1 : countExtraMap.entrySet()) {
                    System.out.println("步骤2");
                    subAddress = subCustomerMap_1.getKey();
                    subCountExtraMap = (Map) subCustomerMap_1.getValue();
                    System.out.println("步骤3");
                    //查询当前小时进店量
                    if(classDataMapper.searchNowHour_in_customer_number(subAddress, subIndoorname)!=null){
                        Integer subHour_customer_this = classDataMapper.searchNowHour_in_customer_number(subAddress, subIndoorname);
                        subHour_customer = subHour_customer_sum - subHour_customer_this;
                        System.out.println("作差后存值：" + subHour_customer);
                    }
                    if (subHour_customer!=null||subCountExtraMap.get("dynamic_customer") != 0 || subCountExtraMap.get("jumpOut_customer") != 0)
                        classDataMapper.updateDataThread(subAddress, subCountExtraMap.get("dynamic_customer"), subCountExtraMap.get("jumpOut_customer"), subHour_customer, subIndoorname);
                }
                System.out.println("步骤4");
            }

        if (subCustomerMap2 != null) {
            latest_time = new Timestamp(System.currentTimeMillis());
            //禁止区域
            for (Map.Entry<String, Object> subCustomerMap_1 : subCustomerMap2.entrySet()) {
                Object subMac = subCustomerMap_1.getKey();
                Map<String, Object> subCustomerMap_2 = (Map) redisUtil.hget("stopvisit", (String) subMac);
                //时间间隔
                Integer countTime = new Long((latest_time.getTime() - (Long) subCustomerMap_2.get("beat")) / (60 * 1000)).intValue();
                //大于1分钟没有心跳的店内客人
                if (countTime >= 1 && (Integer) subCustomerMap_2.get("inJudge") == 1) {
                    Long stayTime = ((Long) subCustomerMap_2.get("left_time") - (Long) subCustomerMap_2.get("in_time")) / 1000;
                    jumpOut_customer2 = 1;  //跳出量+1
                    subCustomerMap_2.put("inJudge", 0); //不在区域内
                    subCustomerMap_2.put("rt", stayTime.toString()); //停留时间
                    visitMapper.updateInjudge2(0, subCustomerMap_2.get("mac").toString(), subCustomerMap_2.get("address").toString(), subCustomerMap_2.get("indoorname").toString());
                } else if (countTime < 1 && (Integer) subCustomerMap_2.get("inJudge") == 1) { //人在室内
                    dynamic_customer2 = 1; //现存人数+1
                }
                subAddress2 = (String) subCustomerMap_2.get("address");
                subIndoorname2 = (String) subCustomerMap_2.get("indoorname");

                if (jumpOut_customer2 != 0 || dynamic_customer2 != 0) {
                    if (countExtraMap2.get(subAddress2) == null) {
                        subCountExtraMap2 = new HashMap<>();
                        subCountExtraMap2.put("jumpOut_customer", jumpOut_customer2);
                        subCountExtraMap2.put("dynamic_customer", dynamic_customer2);
                        countExtraMap2.put(subAddress2, subCountExtraMap2);
                    } else {
                        subCountExtraMap2 = (Map) countExtraMap2.get(subAddress2);
                        subCountExtraMap2.put("jumpOut_customer", subCountExtraMap2.get("jumpOut_customer") + jumpOut_customer2);
                        subCountExtraMap2.put("dynamic_customer", subCountExtraMap2.get("dynamic_customer") + dynamic_customer2);
                        countExtraMap2.put(subAddress2, subCountExtraMap2);
                    }
                }
                jumpOut_customer2 = 0;
                dynamic_customer2 = 0;
                //更新缓存（主要更新inJudge值）,因为要实时获取区域人数，且区域人数是通过inJudge判断，所以也要更新到数据库中
                redisUtil.hset("stopvisit", subMac.toString(), subCustomerMap_2);
            }

            //存到数据库中（遍历上面存储跳出量的map）
            for (Map.Entry<String, Object> subCustomerMap_1 : countExtraMap2.entrySet()) {
                subAddress2 = subCustomerMap_1.getKey();
                subCountExtraMap2 = (Map) subCustomerMap_1.getValue();
                //查询当前小时进店量
                //subHour_customer2 = classDataMapper.searchNowHour_in_customer_number(subAddress2,subIndoorname2);
                Integer subHour_customer_this = 0;
                if(classDataMapper.searchNowHour_in_customer_number(subAddress2, subIndoorname2)!=null){
                    subHour_customer_this = classDataMapper.searchNowHour_in_customer_number(subAddress2, subIndoorname2);
                }
                subHour_customer2 = subHour_customer_sum - subHour_customer_this;
                //如果当前店面人流量大于小时进店量, 则小时客流量等于当前店面人流量
                // if (subCountExtraMap2.get("dynamic_customer")>subHour_customer2)
                //   subHour_customer2 = subCountExtraMap2.get("dynamic_customer");
                if (subHour_customer2!=null||subCountExtraMap2.get("dynamic_customer") != 0 || subCountExtraMap2.get("jumpOut_customer") != 0)
                    classDataMapper.updateDataThread(subAddress2, subCountExtraMap2.get("dynamic_customer"), subCountExtraMap2.get("jumpOut_customer"), subHour_customer2, subIndoorname2);
            }
        }

        //遍历更新设备状态缓存
        Map<String,Object> machineMap = redisUtil.hmget("machineAP");
        if(machineMap!= null){
            for (Map.Entry<String, Object> subMachineMap:machineMap.entrySet()) {
                String subMachineId = (String) subMachineMap.getKey();
                Map<String,Object> subMachineMap_1 = (Map)subMachineMap.getValue();
                    Integer machineCountTime = new Long((latest_time.getTime() - (Long)subMachineMap_1.get("beat")) / (60*1000)).intValue();
                    if (machineCountTime>10)//设备大于10分钟收不到信号则判定为离线
                        subMachineMap_1.put("status","离线");
                    else
                        subMachineMap_1.put("status","在线");
                    redisUtil.hset("machineAP",subMachineId,subMachineMap_1);
                    //将状态存到数据库
                    machineMapper.updateStatus(subMachineMap_1.get("status").toString(),subMachineId);
            }
        }

    }

    //此进程用于存储用户信息和补充跳出量(1小时存一次)--->普通区域
    @Transactional
    @Async
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void saveDataThread(){
        //添加下一个小时的区域时间=======================》数据表小时插入区域的关键
        dataUtil.insertClassData();
        //看数据库发现跳出量与访问量不一致，需要做跳出量补充（先清表重测）===》跨小时造成的，通过每小时补充上一小时的区域在留人数
        List<ClassData> list = classDataService.getPreHour();
        if(list!=null&&list.size()!=0){
            for(int i=0;i<list.size();i++){
                System.out.println("前一个小时的值非空！");
                if(list.get(i)!=null){
                    System.out.println("list的获取值："+list.get(i).toString());
                    Integer jumpout = list.get(i).getJumpOut();
                    String adress = list.get(i).getAdress();
                    String indoorname = list.get(i).getIndoorname();
                    Integer now = list.get(i).getClassNowNumber();
                    Integer in = list.get(i).getInClassNumber();
                    System.out.println("跳出量："+jumpout);
                    System.out.println("现存值："+now);
                    if(jumpout!=in&&now!=0){
                        System.out.println("更新小时差值！"+jumpout+","+adress+","+now);
                        classDataMapper.updateClassDataTwo(adress,now,indoorname);
                    }
                    else{
                        System.out.println("不行！！");
                    }
                }
            }
        }
        else{
            System.out.println("前一个小时的值为空！");
        }
    }

    //此进程用于存储用户信息和补充跳出量(5秒存一次)
    @Transactional
    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void saveDataThread2(){
        //普通区域
        Map<String,Object> subCustomerMap2 = redisUtil.hmget("visit");
        System.out.println("到这里");
        if(subCustomerMap2!=null){
            System.out.println("进来开始存到visit表中");
            //将visit表的缓存存到数据库中
            for (Map.Entry<String, Object> subCustomerMap_1:subCustomerMap2.entrySet()) {
                String subMac = (String) subCustomerMap_1.getKey();
                Map<String,Object> subCustomerMap_2 = (Map<String, Object>) redisUtil.hget("visit", (String) subMac);
                String mac = subCustomerMap_2.get("mac").toString();
                System.out.println("mac值"+mac);
                Timestamp first_in_time = new Timestamp((Long)subCustomerMap_2.get("in_time"));
                Timestamp left_time =  new Timestamp((Long)subCustomerMap_2.get("left_time"));
                Timestamp last_in_time =  new Timestamp((Long)subCustomerMap_2.get("last_in_time"));
                Timestamp beat = new Timestamp((Long)subCustomerMap_2.get("beat"));
                visitMapper.updateCustomer((String) subCustomerMap_2.get("address"),mac,(Integer)subCustomerMap_2.get("rssi"),first_in_time,left_time,beat,(Integer)subCustomerMap_2.get("inJudge"),(Integer)subCustomerMap_2.get("visited_times"),last_in_time,subCustomerMap_2.get("rt").toString(),(String) subCustomerMap_2.get("indoorname"));
                System.out.println("执行成功！");
            }
            //visit表是否还在区域的值改为0
            visitMapper.updateInjudge();
        }

        //禁止区域
        Map<String,Object> subCustomerMap = redisUtil.hmget("stopvisit");
        if(subCustomerMap!=null){
            for (Map.Entry<String, Object> subCustomerMap_1:subCustomerMap.entrySet()) {
                String subMac = (String) subCustomerMap_1.getKey();
                Map<String,Object> subCustomerMap_2 = (Map<String, Object>) redisUtil.hget("stopvisit", (String) subMac);
                String mac = subCustomerMap_2.get("mac").toString();
                Timestamp first_in_time = new Timestamp((Long)subCustomerMap_2.get("in_time"));
                Timestamp left_time =  new Timestamp((Long)subCustomerMap_2.get("left_time"));
                Integer handleJudge =  (Integer)subCustomerMap_2.get("handleJudge");
                Timestamp beat = new Timestamp((Long)subCustomerMap_2.get("beat"));
                stopVisitMapper.updateCustomer((String) subCustomerMap_2.get("address"),mac,(Integer)subCustomerMap_2.get("rssi"),first_in_time,left_time,beat,(Integer)subCustomerMap_2.get("inJudge"),(Integer)subCustomerMap_2.get("visited_times"),handleJudge,subCustomerMap_2.get("rt").toString(),subCustomerMap_2.get("indoorname").toString());
            }
            stopVisitMapper.updateInjudge();
        }
    }

    //此进程用于删除三个月前的数据
    @Transactional
    @Async
    @Scheduled(cron = "* * * * 1/3 ?")
    public void deleteDataThread(){
        classDataMapper.deleteExpiredShop_data();
        visitMapper.deleteExpiredCustomer();
        stopVisitMapper.deleteExpiredCustomer();
    }

    //每1分钟读取一次区域的当前人数
    @Transactional
    @Async
    @Scheduled(cron = "* 0/1 * * * ?")
    public void changeDevice() throws Exception {
           List<String> g = new ArrayList<>();
           String u = (String) redisUtil.get("control");
           if("0".equals(u)){
               System.out.println("若存在即自动控制被关闭，则不执行!!");
           }
           else{
               System.out.println("自动灯开关");
               List<ClassData> classData=classDataService.list();
               Integer count = classDataService.listdis();
               //取最新的前4条处理
               if(classData!=null){
                   //for(ClassData c:classData.subList(0, 4)){

                   for(ClassData c:classData.subList(0, count)){
                       System.out.println(c.getAdress()+"当前人数："+c.getClassNowNumber());
                       List<Device> devices = deviceService.listbyAdressLight(c.getAdress(),c.getIndoorname());

                       if(c.getClassNowNumber().toString().equals("0")){  //该区域当前人数为0则关灯
                           System.out.println("走这："+c.getClassNowNumber().toString());
                           if(devices.size()!=0){ //如果该区域有设备就操作
                               //for(Device d:devices){
                               for(int i=0;i<devices.size();i++){
                                   if(deviceService.listbyId2(devices.get(i).getId())!=null&&deviceService.listbyId2(devices.get(i).getId()).size()!=0){
                                       String kvalue = deviceService.listbyId2(devices.get(i).getId()).get(0).getDevicevalue(); //根据id从device01表获取对应设备最新的当前状态值

                                       //先检验状态是否一致，再进行操作记录插入，防止大量数据插入
                                       if("0".equals(kvalue)){
                                           System.out.println("要操作的状态与当前状态一致，不插入操作记录！！！");
                                       }
                                       else {
                                           if(logrecordService.listbyId(devices.get(i).getId())!=null&&logrecordService.listbyId(devices.get(i).getId()).size()!=0) { //若已有记录
                                               String kvalue2 = logrecordService.listbyId(devices.get(i).getId()).get(0).getChangevalue(); //根据id从logrecord01表获取对应设备最新的操作状态值
                                               if ("0".equals(kvalue2)) {
                                                   //即将要插入的操作记录与当前已存在的操作记录相同则不进行插入，防止多次插入
                                               } else {
                                                   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                                                   String dt = df.format(new Date());//获取当前系统时间并格式化
                                                   logrecordService.addchange(devices.get(i).getId(), "0", dt, devices.get(i).getIndoorname());
                                               }
                                           }
                                           else {
                                               SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                                               String dt = df.format(new Date());//获取当前系统时间并格式化
                                               logrecordService.addchange(devices.get(i).getId(), "0", dt, devices.get(i).getIndoorname());
                                           }
                                       }
                                   }
                               }
                           }
                       }
                       else{ //该区域当前人数非0则开灯
                           if(devices.size()!=0){
                               for(int i=0;i<devices.size();i++){
                                   if(deviceService.listbyId2(devices.get(i).getId())!=null&&deviceService.listbyId2(devices.get(i).getId()).size()!=0) {
                                       String kvalue = deviceService.listbyId2(devices.get(i).getId()).get(0).getDevicevalue(); //根据id从device01表获取对应设备最新的当前状态值

                                       //先检验状态是否一致，再进行操作记录插入，防止大量数据插入
                                       if ("1".equals(kvalue)) {
                                           System.out.println("要操作的状态与当前状态一致，不插入操作记录！！！");
                                       } else {
                                           if(logrecordService.listbyId(devices.get(i).getId())!=null&&logrecordService.listbyId(devices.get(i).getId()).size()!=0) {  //若已有记录
                                               String kvalue2 = logrecordService.listbyId(devices.get(i).getId()).get(0).getChangevalue(); //根据id从logrecord01表获取对应设备最新的操作状态值
                                               if ("1".equals(kvalue2)) {
                                                   //即将要插入的操作记录与当前已存在的操作记录相同则不进行插入，防止多次插入
                                               } else {
                                                   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                                                   String dt = df.format(new Date());//获取当前系统时间并格式化
                                                   System.out.println("写入设备控制状态");
                                                   logrecordService.addchange(devices.get(i).getId(), "1", dt, devices.get(i).getIndoorname());
                                               }
                                           }
                                           else {
                                               SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                                               String dt = df.format(new Date());//获取当前系统时间并格式化
                                               System.out.println("写入设备控制状态");
                                               logrecordService.addchange(devices.get(i).getId(), "1", dt, devices.get(i).getIndoorname());
                                           }
                                       }
                                   }
                               }
                           }
                       }
                   }

               }
           }
        List<Device> devices2 = deviceService.list();
        for(Device d:devices2){
            //把所有要操作的设备id加入到redis的list中
            g.add(d.getId());
        }
        System.out.println("叠加后的设备列表："+g);
        redisUtil.del("MachineList");
        redisUtil.lSet("MachineList",g);
        System.out.println("取redis中的值："+redisUtil.lGet("MachineList",0,-1)+"====>类型："+ redisUtil.lGet("MachineList",0,-1).getClass().toString());
        return;
    }



    class Socket1 implements Runnable
    {
        public void run()
        {
            try
            {
                System.out.println("t1线程启动！");
                //启动智能硬件设备信号接收
                deviceService.monitor();
            }
            catch(Exception e)
            {
                System.out.println("Error");
            }
        }
    }

    class Socket2 implements Runnable
    {
        public void run()
        {
            try
            {
                System.out.println("t2线程启动！");
                //监听器
                ds = new DatagramSocket(PORT);
                System.out.println("等待链接");
                buf = new byte[1024];
                dp = new DatagramPacket(buf, buf.length);
                //初始化设备表区域数据表，将表数据放缓存中（特别注意初始化后只有4条，想要后续每小时插入4条进行数据统计，就要用到异步线程的定时操作）
                dataUtil.refreshMachineCache();
                dataUtil.initClassData();
                System.out.println("初始化完成");

                //自动控制默认是开启状态
                redisUtil.set("control","1");

                while (true) {
                    //synchronized关键字是用来控制线程同步的，就是在多线程的环境下，控制synchronized代码段不被多个线程同时执行
                    synchronized (this) {
                        try {
                            //获取wifi探针数据
                            ds.receive(dp);
                            strReceive = new String(dp.getData());
                            System.out.println("接收到的值："+strReceive);
                            //转成json对象取值
                            jsonObject = JSON.parseObject(strReceive);
                            if (jsonObject != null) {
                                //把数据初步分析出来
                                //indoorname = jsonObject.getString("indoorname").trim();
                                machineId = jsonObject.getString("Id");
                                data = jsonObject.getString("Data");
                                if(machineService.listbyId2(machineId).size()!=0&&machineService.listbyId2(machineId)!=null){
                                    indoorname = machineService.listbyId2(machineId).get(0).getIndoorname();
                                    System.out.println("machineId："+machineId);
                                    System.out.println("设备id是否匹配格式要求："+machineId.matches(idM));
                                    if (data != null) {
                                        jsonArray = JSONArray.parseArray(data);
                                        System.out.println("jsonArray："+jsonArray);
                                    }
                                    else{
                                        System.out.println("继续循环");
                                        continue;
                                    }

                                    dataUtil.get(machineId);  //测试用
                                    //如果设备在后台管理系统没有被添加,则不接受处理
                                    if (machineId.matches(idM) && dataUtil.getmachineAP(machineId) != null) {
                                        System.out.println("更新设备beat");
                                        dataUtil.refreshMachineCacheBeat(machineId);
                                        System.out.println("进来进行处理");
                                        dataSize = jsonArray.size();/**分析各个mac的数据，拆分data值**/

                                        nowmachineid=machineId;
                                        nowindoorname=indoorname;//确保在一个地图

                                        System.out.println("nowmachineid："+nowmachineid);
                                        System.out.println("lastmachineid："+lastmachineid);
                                        if(!nowmachineid.equals(lastmachineid)) {   //判断4台均为不同的设备
                                            APcount += 1;
                                            System.out.println("APcount："+APcount);
                                        }
                                        for (int i = 0; i < dataSize; i++) {
                                            jsonObjectData = jsonArray.getJSONObject(i);
                                            mac = jsonObjectData.getString("mac").toString();
                                            rssi = jsonObjectData.getInteger("rssi");
                                            System.out.println("mac和rssi："+mac+"-"+rssi);
                                            //筛选
                                            if (mac != null && mac.matches(macM) && rssi != null && rssi.toString().matches(rssiM) && !mac.startsWith("00:00") && rssi > (Integer) ((Map) redisUtil.hget("machineAP", machineId)).get("leastRssi")) {
                                                System.out.println("进来了！！");
                                                Map<String,Object> machineMap = new HashMap<>();
                                                //获取当前时间
                                                Calendar d=Calendar.getInstance(TimeZone.getTimeZone("GMT:+08:00"));
                                                //MacSortByDate macSortByDate = new MacSortByDate();
                                                //macSortByDate.setRssi(rssi);
                                                //macSortByDate.setTime(d.getTime());
                                                //macSortByDate.setMachineId(machineId);
                                                machineMap.put("rssi",rssi);
                                                machineMap.put("time",d.getTime());
                                                machineMap.put("machineId",machineId);
                                                //设置mac-machine-rssi缓存（key--项--值）：再次插入的缓存中当key、项相同时会把值覆盖掉（当人不断发生位移时，同一个AP收到同一个人的rssi信号每次都会不同的场景）。HSET过去只能设置一个键值对，如果需要一次设置多个，则必须使用HMSET（M表示多重）
                                                //序列化java对象，并储存到Redis
                                                //byte[] serialize = SerializeUtil.serialize(macSortByDate);
                                                redisUtil.hset(mac,machineId,machineMap);
                                            } else{
                                                System.out.println("继续循环");
                                                continue;
                                            }

                                            if(APcount >3 && redisUtil.hmget(mac).size() > 3 && nowindoorname.equals(lastindoorname)) { //该mac至少有4个rssi了 ,AP>3或缓存中的键值对>3，且在同一个地图
                                                /**开始计算（x,y）**/
                                                //取mac缓存中rssi信号最强的前4个计算
                                                System.out.println("开始计算（x,y）");
                                                Map<String, Object> map = redisUtil.hmget(mac);  //由项返回多个键值对
                                                List<Map.Entry<String, Map<String, Object>>> list = new LinkedList(map.entrySet());//将map变成list,取出来的数据放在map中是无序的所以要给它排下序
                                                // 下面的也可以写成lambda表达式这种形式：Collections.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

                                                Collections.sort(list, new Comparator<Map.Entry<String, Map<String, Object>>>() {
                                                    @Override
                                                    public int compare(Map.Entry<String, Map<String, Object>> o1, Map.Entry<String, Map<String, Object>> o2) {
                                                        Long ob1 = (Long) o1.getValue().get("time");
                                                        Long ob2 = (Long) o2.getValue().get("time");
                                                        return ob2.compareTo(ob1); // 这里为降序排序，这里也可以改成根据key和value进行排序
                                                    }
                                                });


                                                System.out.println("取后四条数据,并存value");
                                                List<Map<String, Object>> aplist = new ArrayList<>();
                                                for (Map.Entry<String, Map<String, Object>> p : list.subList(0, 4)) {  //key：设备id，值：rssi
                                                    System.out.println(p.getKey() + "===============》 " + p.getValue().get("rssi"));
                                                    aplist.add((Map) redisUtil.hget("machineAP", p.getKey()));//返回的是value,取rssi
                                                }
                                                //进行4选3排列AP组合
                                                dataUtil.reSort(aplist, 3, 0, 0);
                                                List<List<Map<String, Object>>> sortResult = dataUtil.getStu3();
                                                Double totalX = 0.0;
                                                Double totalY = 0.0;
                                                Map<String, Double> respoint = new HashMap<>();
                                                Map<String, Integer> macpoint = new HashMap<>();
                                                for (List<Map<String, Object>> sort : sortResult) { //[[1, 2, 3], [1, 2, 4], [1, 3, 4], [2, 3, 4]]
                                                    respoint = dataUtil.CaculateByAPList(sort, mac);  //传[1, 2, 3]
                                                    totalX += respoint.get("x");
                                                    totalY += respoint.get("y");
                                                }
                                                macpoint = dataUtil.cpoint(totalX, totalY, atAddress);//传入加权后的坐标，输出平均值坐标
                                                System.out.println("人的位置坐标"+macpoint);

                                                //根据区域x、y的范围值，判断在哪个区域
                                                atAddress = dataUtil.judgeClass(macpoint.get("macx"), macpoint.get("macy"),indoorname);
                                                System.out.println("人所在区域："+atAddress);

                                                //根据区域名判断是否为禁止区域
                                                StopJudege = dataUtil.Stopjudge(atAddress,indoorname);
                                                System.out.println("是否为禁止区域："+StopJudege);
                                                //当前时间戳
                                                latest_time = new Timestamp(System.currentTimeMillis());

                                                lastx=locationMapper.searchLocationX(mac);
                                                lasty=locationMapper.searchLocationY(mac);
                                                nowx=macpoint.get("macx").toString();
                                                nowy=macpoint.get("macy").toString();
                                                if(lastx!=null&&lasty!=null){
                                                    //当与上一个位置不同时,将人的位置存到数据库
                                                    if(lastx.equals(nowx)&&lasty.equals(nowy)){
                                                        System.out.println("在同一位置上没有移动");
                                                    }
                                                    else {
                                                        locationMapper.insertLocation(mac,atAddress,nowx,nowy,indoorname);
                                                    }
                                                }
                                                else {
                                                    locationMapper.insertLocation(mac,atAddress,nowx,nowy,indoorname);
                                                }


                                                /**先判断是否存在，注意：同一个区域的同一个mac用更新方式，否则插入**/
                                                //不存在的情况
                                                if (!dataUtil.checkExist(mac, atAddress,indoorname)) {
                                                    //新客人
                                                    System.out.println("新客人");
                                                    if (StopJudege == 0) {
                                                        //加入新客人
                                                        dataUtil.insertMac(atAddress, 1, 1, mac, rssi,indoorname);
                                                    } else {
                                                        dataUtil.insertStopMac(atAddress, 1, 1, mac, rssi,indoorname);
                                                    }
                                                    //加入缓存
                                                    new_student++;
                                                    in_class_number++;
                                                    hour_in_class_number++;
                                                }
                                                //mac在普通区域或禁区已存在的情况
                                                else {
                                                    System.out.println("跑这里去");
                                                    //更新的是人所在区域的mac信息
                                                    if (StopJudege == 0) { //所在区域为普通区域
                                                        macMap = dataUtil.getMacMap(mac, atAddress,indoorname);  //获取缓存进行处理，没有就从数据库中同步进来
                                                        if(macMap!=null){
                                                            Iterator<Map.Entry<String, Object>> entries = macMap.entrySet().iterator();
                                                            while(entries.hasNext()){
                                                                Map.Entry<String, Object> entry = entries.next();
                                                                String key = entry.getKey();
                                                                Object value = entry.getValue();
                                                                System.out.println("key和value是："+key+":"+value);
                                                            }

                                                            timeCount = new Long((latest_time.getTime() - (Long) macMap.get("beat")) / (60 * 1000)).intValue();
                                                            //出现间隔（AP再次探测到的时间-上次在店心跳）大于1分钟,再进算进入区域量+1（离开5分钟后再进来相当于再次访问，而1分钟内连续访问的不算是再进）
                                                            if (timeCount >= 1&&(Integer) macMap.get("inJudge") == 0){  //大于1分钟AP检测不到mac心跳视为之前人跑到了室外，然后再重新跑进来才被检测到
                                                                //上次进店时间为上一次的first_in_time
                                                                macMap.put("last_in_time", (Long) macMap.get("in_time"));
                                                                macMap.put("in_time", latest_time);
                                                                macMap.put("visited_times", (Integer) macMap.get("visited_times") + 1);
                                                                in_class_number++;
                                                                hour_in_class_number++;
                                                                //visitMapper.updateInjudge2(0,mac,atAddress);
                                                            }

                                                            //出现间隔小于1分钟视为一直在室内（为了方便处理在多个区域间频繁移动的人）
                                                            macMap.put("left_time", latest_time);
                                                            macMap.put("beat", latest_time);
                                                            macMap.put("inJudge", 1);
                                                            macMap.put("rssi", rssi);
                                                            //更新cache信息
                                                            dataUtil.refreshMacCache(mac, atAddress,macMap,indoorname);
                                                            visitMapper.updateInjudge2(1,mac,atAddress,indoorname);
                                                        }

                                                    } else { //所在区域为禁区
                                                        macMap = dataUtil.getStopMacMap(mac, atAddress,indoorname);
                                                        timeCount = new Long((latest_time.getTime() - (Long) macMap.get("beat")) / (60 * 1000)).intValue();
                                                        if (timeCount >= 1&&(Integer) macMap.get("inJudge") == 0) {
                                                            macMap.put("in_time", latest_time);
                                                            macMap.put("visited_times", (Integer) macMap.get("visited_times") + 1);
                                                            in_class_number++;
                                                            hour_in_class_number++;
                                                            //stopVisitMapper.updateInjudge2(0,mac,atAddress);  //将缓存数据更新到数据库中
                                                        }
                                                        macMap.put("handleJudge", 0);
                                                        macMap.put("left_time", latest_time); //离开时间等价于最后一次在店时间latest_in_time
                                                        macMap.put("beat", latest_time);
                                                        macMap.put("inJudge", 1);
                                                        macMap.put("rssi", rssi);
                                                        //更新cache信息
                                                        dataUtil.refreshStopMacCache(mac,atAddress, macMap,indoorname);
                                                        stopVisitMapper.updateInjudge2(1,mac,atAddress,indoorname);
                                                        System.out.println("更新cache信息完成！");
                                                    }
                                                }

                                                //位置计算完毕清除mac缓存
                                                // redisUtil.del(mac);
                                                dataUtil.clearstu2();

                                            }
                                            else {
                                                System.out.println("不满足不同AP的4个rssi的条件");
                                                continue;  //继续循环，跳过本次循环体中余下尚未执行的语句，立即进行下一次的循环条件
                                            }
                                        }
                                        //这里更新
                                        if(atAddress!=null){
                                            if (new_student != 0 || in_class_number != 0 || hour_in_class_number != 0 ) {
                                                classDataMapper.updateClassData(atAddress, new_student, in_class_number, hour_in_class_number,indoorname);//倒序排序更新最新的那条
                                            }
                                        }

                                        System.out.println("更新统计数据值完成");
                                        new_student = 0;
                                        in_class_number = 0;
                                        hour_in_class_number = 0;

                                        lastmachineid=nowmachineid;
                                        lastindoorname=nowindoorname;

                                        if(APcount>3){
                                            APcount = 0; //记得将数据清0，否则会一直累加
                                        }
                                    }
                                    else{
                                        System.out.println("设备在后台没添加");
                                    }
                                }
                                else {
                                    System.out.println("不执行");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch(Exception e)
            {
                System.out.println("Error");
            }finally {
                ds.close();
            }
        }
    }

}


