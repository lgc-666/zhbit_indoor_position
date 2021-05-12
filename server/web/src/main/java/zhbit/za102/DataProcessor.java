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
    private final static int PORT = 3021;
    private DatagramSocket ds;
    private DatagramPacket dp;
    private byte[] buf = null;
    private String strReceive;

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
    private String data;
    private Integer dataSize;
    private Map<String, Object> macMap;
    private Integer timeCount;
    private Timestamp latest_time;
    private Integer visited_times;
    private Integer StopJudege;
    private String atAddress;  //所在区域

    private Integer APcount = 0;

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
        }
    }

    @Transactional
    @Async
    @Scheduled(cron = "0/3 * * * * ?")
    public void dataThread() {
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
        Map<String, Object> subCountExtraMap;
        Map<String, Object> subCustomerMap = redisUtil.hmget("visit");
        Map<String, Object> countExtraMap2 = new HashMap<>();
        Map<String, Object> subCountExtraMap2;
        Map<String, Object> subCustomerMap2 = redisUtil.hmget("stopvisit");

/*        List<Class> a = classService.list();
        for (Class b : a) {
            if(classDataMapper.searchNowHour_in_customer_number(b.getAdress(), b.getIndoorname())!=null){
                subHour_customer_sum = subHour_customer_sum + classDataMapper.searchNowHour_in_customer_number(b.getAdress(), b.getIndoorname());
            }
        }*/

        if (subCustomerMap != null) {
                latest_time = new Timestamp(System.currentTimeMillis());
                //普通区域
                for (Map.Entry<String, Object> subCustomerMap_1 : subCustomerMap.entrySet()) {
                    Object subMac = subCustomerMap_1.getKey();
                    Map<String, Object> subCustomerMap_2 = (Map) redisUtil.hget("visit", (String) subMac);
                    Integer countTime = new Long((latest_time.getTime() - (Long) subCustomerMap_2.get("beat")) / (60 * 1000)).intValue();

                    if (countTime >= 1 && (Integer) subCustomerMap_2.get("inJudge") == 1) {
                        Long stayTime = ((Long) subCustomerMap_2.get("left_time") - (Long) subCustomerMap_2.get("in_time")) / 1000;
                        jumpOut_customer = 1;
                        subCustomerMap_2.put("inJudge", 0); //不在区域内
                        visitMapper.updateInjudge2(0, subCustomerMap_2.get("mac").toString(), subCustomerMap_2.get("address").toString(), subCustomerMap_2.get("indoorname").toString());
                        subCustomerMap_2.put("rt", stayTime.toString());
                        System.out.println("不在室内");
                    }
                    if (countTime < 1 && (Integer) subCustomerMap_2.get("inJudge") == 1) { //人在室内
                        System.out.println("在室内");
                        dynamic_customer = 1;
                    }
                    subAddress = (String) subCustomerMap_2.get("address");
                    subIndoorname = (String) subCustomerMap_2.get("indoorname");
                    if (jumpOut_customer != 0 || dynamic_customer != 0) {
                        if (countExtraMap.get(subAddress) == null) {
                            subCountExtraMap = new HashMap<>();
                            subCountExtraMap.put("jumpOut_customer", jumpOut_customer);
                            subCountExtraMap.put("dynamic_customer", dynamic_customer);
                            subCountExtraMap.put("indoorname",subIndoorname);
                            countExtraMap.put(subAddress, subCountExtraMap);
                        } else {
                            subCountExtraMap = (Map) countExtraMap.get(subAddress);
                            subCountExtraMap.put("jumpOut_customer", (Integer)subCountExtraMap.get("jumpOut_customer") + jumpOut_customer);
                            subCountExtraMap.put("dynamic_customer", (Integer)subCountExtraMap.get("dynamic_customer") + dynamic_customer);
                            subCountExtraMap.put("indoorname",subIndoorname);
                            countExtraMap.put(subAddress, subCountExtraMap);
                        }
                    }
                    System.out.println(subAddress+"-----"+subIndoorname+"-->现存人数："+dynamic_customer);

                    jumpOut_customer = 0;
                    dynamic_customer = 0;
                    redisUtil.hset("visit", subMac.toString(), subCustomerMap_2);
                }

                //存到数据库中（遍历上面存储跳出量的map）
                for (Map.Entry<String, Object> subCustomerMap_1 : countExtraMap.entrySet()) {
                    subAddress = subCustomerMap_1.getKey();
                    subCountExtraMap = (Map) subCustomerMap_1.getValue();

                    //获取指定室内场所的数据，并减去当前区域的数据
                    List<Class> a = classService.list3(subCountExtraMap.get("indoorname").toString());
                    subHour_customer_sum=0;
                    for (Class b : a) {
                        if(classDataMapper.searchNowHour_in_customer_number(b.getAdress(), b.getIndoorname())!=null){
                            subHour_customer_sum = subHour_customer_sum + classDataMapper.searchNowHour_in_customer_number(b.getAdress(), b.getIndoorname());
                            System.out.println("sub_sum："+subHour_customer_sum);
                        }
                    }

                    //查询当前小时进店量
                    if(classDataMapper.searchNowHour_in_customer_number(subAddress, subCountExtraMap.get("indoorname").toString())!=null){
                        Integer subHour_customer_this = classDataMapper.searchNowHour_in_customer_number(subAddress, subCountExtraMap.get("indoorname").toString());
                        subHour_customer = subHour_customer_sum - subHour_customer_this;
                        System.out.println("sub_customer_this："+subHour_customer_this);
                    }
                    if (subHour_customer!=null||(Integer) subCountExtraMap.get("dynamic_customer") != 0 || (Integer) subCountExtraMap.get("jumpOut_customer") != 0)
                        classDataMapper.updateDataThread(subAddress, (Integer) subCountExtraMap.get("dynamic_customer"), (Integer) subCountExtraMap.get("jumpOut_customer"), subHour_customer, subCountExtraMap.get("indoorname").toString());
                }
            }

        if (subCustomerMap2 != null) {
            latest_time = new Timestamp(System.currentTimeMillis());
            //禁止区域
            for (Map.Entry<String, Object> subCustomerMap_1 : subCustomerMap2.entrySet()) {
                Object subMac = subCustomerMap_1.getKey();
                Map<String, Object> subCustomerMap_2 = (Map) redisUtil.hget("stopvisit", (String) subMac);
                Integer countTime = new Long((latest_time.getTime() - (Long) subCustomerMap_2.get("beat")) / (60 * 1000)).intValue();
                if (countTime >= 1 && (Integer) subCustomerMap_2.get("inJudge") == 1) {
                    Long stayTime = ((Long) subCustomerMap_2.get("left_time") - (Long) subCustomerMap_2.get("in_time")) / 1000;
                    jumpOut_customer2 = 1;
                    subCustomerMap_2.put("inJudge", 0); //不在区域内
                    subCustomerMap_2.put("rt", stayTime.toString());
                    visitMapper.updateInjudge2(0, subCustomerMap_2.get("mac").toString(), subCustomerMap_2.get("address").toString(), subCustomerMap_2.get("indoorname").toString());
                } else if (countTime < 1 && (Integer) subCustomerMap_2.get("inJudge") == 1) { //人在室内
                    dynamic_customer2 = 1;
                }
                subAddress2 = (String) subCustomerMap_2.get("address");
                subIndoorname2 = (String) subCustomerMap_2.get("indoorname");

                if (jumpOut_customer2 != 0 || dynamic_customer2 != 0) {
                    if (countExtraMap2.get(subAddress2) == null) {
                        subCountExtraMap2 = new HashMap<>();
                        subCountExtraMap2.put("jumpOut_customer", jumpOut_customer2);
                        subCountExtraMap2.put("dynamic_customer", dynamic_customer2);
                        subCountExtraMap2.put("indoorname",subIndoorname2);
                        countExtraMap2.put(subAddress2, subCountExtraMap2);
                    } else {
                        subCountExtraMap2 = (Map) countExtraMap2.get(subAddress2);
                        subCountExtraMap2.put("jumpOut_customer", (Integer)subCountExtraMap2.get("jumpOut_customer") + jumpOut_customer2);
                        subCountExtraMap2.put("dynamic_customer", (Integer)subCountExtraMap2.get("dynamic_customer") + dynamic_customer2);
                        subCountExtraMap2.put("indoorname",subIndoorname2);
                        countExtraMap2.put(subAddress2, subCountExtraMap2);
                    }
                }
                jumpOut_customer2 = 0;
                dynamic_customer2 = 0;
                redisUtil.hset("stopvisit", subMac.toString(), subCustomerMap_2);
            }

            for (Map.Entry<String, Object> subCustomerMap_1 : countExtraMap2.entrySet()) {
                subAddress2 = subCustomerMap_1.getKey();
                subCountExtraMap2 = (Map) subCustomerMap_1.getValue();
                Integer subHour_customer_this = 0;
                //获取指定室内场所的数据，并减去当前区域的数据
                List<Class> a = classService.list3(subCountExtraMap2.get("indoorname").toString());
                subHour_customer_sum=0;
                for (Class b : a) {
                    if(classDataMapper.searchNowHour_in_customer_number(b.getAdress(), b.getIndoorname())!=null){
                        subHour_customer_sum = subHour_customer_sum + classDataMapper.searchNowHour_in_customer_number(b.getAdress(), b.getIndoorname());
                        System.out.println("sub_sum2："+subHour_customer_sum);
                    }
                }
                if(classDataMapper.searchNowHour_in_customer_number(subAddress2, subCountExtraMap2.get("indoorname").toString())!=null){
                    subHour_customer_this = classDataMapper.searchNowHour_in_customer_number(subAddress2, subCountExtraMap2.get("indoorname").toString());
                }
                subHour_customer2 = subHour_customer_sum - subHour_customer_this;
                if (subHour_customer2!=null||(Integer)subCountExtraMap2.get("dynamic_customer") != 0 || (Integer)subCountExtraMap2.get("jumpOut_customer") != 0)
                    classDataMapper.updateDataThread(subAddress2, (Integer)subCountExtraMap2.get("dynamic_customer"), (Integer)subCountExtraMap2.get("jumpOut_customer"), subHour_customer2, subCountExtraMap2.get("indoorname").toString());
            }
        }

        //更新设备状态
        Map<String,Object> machineMap = redisUtil.hmget("machineAP");
        if(machineMap!= null){
            for (Map.Entry<String, Object> subMachineMap:machineMap.entrySet()) {
                String subMachineId = (String) subMachineMap.getKey();
                Map<String,Object> subMachineMap_1 = (Map)subMachineMap.getValue();
                    Integer machineCountTime = new Long((latest_time.getTime() - (Long)subMachineMap_1.get("beat")) / (60*1000)).intValue();
                    if (machineCountTime>10)
                        subMachineMap_1.put("status","离线");
                    else
                        subMachineMap_1.put("status","在线");
                    redisUtil.hset("machineAP",subMachineId,subMachineMap_1);
                    machineMapper.updateStatus(subMachineMap_1.get("status").toString(),subMachineId);
            }
        }

    }

    //此进程用于存储用户信息和补充跳出量
    @Transactional
    @Async
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void saveDataThread(){
        dataUtil.insertClassData();
        List<ClassData> list = classDataService.getPreHour();
        if(list!=null&&list.size()!=0){
            for(int i=0;i<list.size();i++){
                if(list.get(i)!=null){
                    Integer jumpout = list.get(i).getJumpOut();
                    String adress = list.get(i).getAdress();
                    String indoorname = list.get(i).getIndoorname();
                    Integer now = list.get(i).getClassNowNumber();
                    Integer in = list.get(i).getInClassNumber();
                    if(jumpout!=in&&now!=0){
                        classDataMapper.updateClassDataTwo(adress,now,indoorname);
                    }
                    else{
                    }
                }
            }
        }
        else{
        }
    }

    //此进程用于存储用户信息
    @Transactional
    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void saveDataThread2(){
        //普通区域
        Map<String,Object> subCustomerMap2 = redisUtil.hmget("visit");
        if(subCustomerMap2!=null){
            //将visit表的缓存存到数据库中
            for (Map.Entry<String, Object> subCustomerMap_1:subCustomerMap2.entrySet()) {
                String subMac = (String) subCustomerMap_1.getKey();
                Map<String,Object> subCustomerMap_2 = (Map<String, Object>) redisUtil.hget("visit", (String) subMac);
                String mac = subCustomerMap_2.get("mac").toString();
                Timestamp first_in_time = new Timestamp((Long)subCustomerMap_2.get("in_time"));
                Timestamp left_time =  new Timestamp((Long)subCustomerMap_2.get("left_time"));
                Timestamp last_in_time =  new Timestamp((Long)subCustomerMap_2.get("last_in_time"));
                Timestamp beat = new Timestamp((Long)subCustomerMap_2.get("beat"));
                visitMapper.updateCustomer((String) subCustomerMap_2.get("address"),mac,(Integer)subCustomerMap_2.get("rssi"),first_in_time,left_time,beat,(Integer)subCustomerMap_2.get("inJudge"),(Integer)subCustomerMap_2.get("visited_times"),last_in_time,subCustomerMap_2.get("rt").toString(),(String) subCustomerMap_2.get("indoorname"));
            }
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

    //自动控制
    @Transactional
    @Async
    @Scheduled(cron = "* 0/1 * * * ?")
    public void changeDevice() throws Exception {
           List<String> g = new ArrayList<>();
           String u = (String) redisUtil.get("control");
           if("0".equals(u)){
           }
           else{
               List<ClassData> classData=classDataService.list();
               Integer count = classDataService.listdis();
               //取最新的前4条处理
               if(classData!=null){
                   //for(ClassData c:classData.subList(0, 4)){

                   for(ClassData c:classData.subList(0, count)){
                       List<Device> devices = deviceService.listbyAdressLight(c.getAdress(),c.getIndoorname());

                       if(c.getClassNowNumber().toString().equals("0")){
                           if(devices.size()!=0){ //如果该区域有设备就操作
                               for(int i=0;i<devices.size();i++){
                                   if(deviceService.listbyId2(devices.get(i).getId())!=null&&deviceService.listbyId2(devices.get(i).getId()).size()!=0){
                                       String kvalue = deviceService.listbyId2(devices.get(i).getId()).get(0).getDevicevalue();

                                       if("0".equals(kvalue)){
                                       }
                                       else {
                                           if(logrecordService.listbyId(devices.get(i).getId())!=null&&logrecordService.listbyId(devices.get(i).getId()).size()!=0) {
                                               String kvalue2 = logrecordService.listbyId(devices.get(i).getId()).get(0).getChangevalue();
                                               if ("0".equals(kvalue2)) {
                                               } else {
                                                   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                   String dt = df.format(new Date());
                                                   logrecordService.addchange(devices.get(i).getId(), "0", dt, devices.get(i).getIndoorname());
                                               }
                                           }
                                           else {
                                               SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                               String dt = df.format(new Date());
                                               logrecordService.addchange(devices.get(i).getId(), "0", dt, devices.get(i).getIndoorname());
                                           }
                                       }
                                   }
                               }
                           }
                       }
                       else{
                           if(devices.size()!=0){
                               for(int i=0;i<devices.size();i++){
                                   if(deviceService.listbyId2(devices.get(i).getId())!=null&&deviceService.listbyId2(devices.get(i).getId()).size()!=0) {
                                       String kvalue = deviceService.listbyId2(devices.get(i).getId()).get(0).getDevicevalue();

                                       if ("1".equals(kvalue)) {
                                       } else {
                                           if(logrecordService.listbyId(devices.get(i).getId())!=null&&logrecordService.listbyId(devices.get(i).getId()).size()!=0) {
                                               String kvalue2 = logrecordService.listbyId(devices.get(i).getId()).get(0).getChangevalue();
                                               if ("1".equals(kvalue2)) {
                                               } else {
                                                   SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                                   String dt = df.format(new Date());
                                                   logrecordService.addchange(devices.get(i).getId(), "1", dt, devices.get(i).getIndoorname());
                                               }
                                           }
                                           else {
                                               SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                               String dt = df.format(new Date());
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
            g.add(d.getId());
        }
        redisUtil.del("MachineList");
        redisUtil.lSet("MachineList",g);
        return;
    }



    class Socket1 implements Runnable
    {
        public void run()
        {
            try
            {
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
                ds = new DatagramSocket(PORT);
                buf = new byte[1024];
                dp = new DatagramPacket(buf, buf.length);
                dataUtil.refreshMachineCache();
                dataUtil.initClassData();
                redisUtil.set("control","1");

                while (true) {
                   synchronized (this) {
                        try {
                            ds.receive(dp);
                            strReceive = new String(dp.getData());
                            jsonObject = JSON.parseObject(strReceive);
                            if (jsonObject != null) {
                                machineId = jsonObject.getString("Id");
                                data = jsonObject.getString("Data");
                                if(machineService.listbyId2(machineId).size()!=0&&machineService.listbyId2(machineId)!=null){
                                    indoorname = machineService.listbyId2(machineId).get(0).getIndoorname();
                                    if (data != null) {
                                        jsonArray = JSONArray.parseArray(data);
                                    }
                                    else{
                                        continue;
                                    }

                                    dataUtil.get(machineId);
                                    if (machineId.matches(idM) && dataUtil.getmachineAP(machineId) != null) {
                                        dataUtil.refreshMachineCacheBeat(machineId);
                                        dataSize = jsonArray.size();

                                        nowmachineid=machineId;
                                        nowindoorname=indoorname;

                                        System.out.println("nowmachineid："+nowmachineid);
                                        System.out.println("lastmachineid："+lastmachineid);

                                        //判断4台均为不同的设备
                                        if(!nowmachineid.equals(lastmachineid)) {
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
                                                Map<String,Object> machineMap = new HashMap<>();
                                                Calendar d=Calendar.getInstance(TimeZone.getTimeZone("GMT:+08:00"));
                                                machineMap.put("rssi",rssi);
                                                machineMap.put("time",d.getTime());
                                                machineMap.put("machineId",machineId);
                                                redisUtil.hset(mac,machineId,machineMap);
                                            } else{
                                                continue;
                                            }

                                            if(APcount >3 && redisUtil.hmget(mac).size() > 3 && nowindoorname.equals(lastindoorname)) {
                                                Map<String, Object> map = redisUtil.hmget(mac);
                                                List<Map.Entry<String, Map<String, Object>>> list = new LinkedList(map.entrySet());

                                                Collections.sort(list, new Comparator<Map.Entry<String, Map<String, Object>>>() {
                                                    @Override
                                                    public int compare(Map.Entry<String, Map<String, Object>> o1, Map.Entry<String, Map<String, Object>> o2) {
                                                        Long ob1 = (Long) o1.getValue().get("time");
                                                        Long ob2 = (Long) o2.getValue().get("time");
                                                        return ob2.compareTo(ob1);
                                                    }
                                                });


                                                List<Map<String, Object>> aplist = new ArrayList<>();
                                                for (Map.Entry<String, Map<String, Object>> p : list.subList(0, 4)) {
                                                    aplist.add((Map) redisUtil.hget("machineAP", p.getKey()));
                                                }
                                                //排列组合
                                                dataUtil.reSort(aplist, 3, 0, 0);
                                                List<List<Map<String, Object>>> sortResult = dataUtil.getStu3();
                                                Double totalX = 0.0;
                                                Double totalY = 0.0;
                                                Map<String, Double> respoint = new HashMap<>();
                                                Map<String, Integer> macpoint = new HashMap<>();
                                                for (List<Map<String, Object>> sort : sortResult) {
                                                    respoint = dataUtil.CaculateByAPList(sort, mac);
                                                    totalX += respoint.get("x");
                                                    totalY += respoint.get("y");
                                                }
                                                macpoint = dataUtil.cpoint(totalX, totalY, atAddress);

                                                //判断在哪个区域
                                                atAddress = dataUtil.judgeClass(macpoint.get("macx"), macpoint.get("macy"),indoorname);

                                                //根据区域名判断是否为禁止区域
                                                StopJudege = dataUtil.Stopjudge(atAddress,indoorname);

                                                latest_time = new Timestamp(System.currentTimeMillis());

                                                lastx=locationMapper.searchLocationX(mac);
                                                lasty=locationMapper.searchLocationY(mac);
                                                nowx=macpoint.get("macx").toString();
                                                nowy=macpoint.get("macy").toString();
                                                if(lastx!=null&&lasty!=null){
                                                    if(lastx.equals(nowx)&&lasty.equals(nowy)){
                                                    }
                                                    else {
                                                        locationMapper.insertLocation(mac,atAddress,nowx,nowy,indoorname);
                                                    }
                                                }
                                                else {
                                                    locationMapper.insertLocation(mac,atAddress,nowx,nowy,indoorname);
                                                }


                                                if (!dataUtil.checkExist(mac, atAddress,indoorname)) {
                                                    if (StopJudege == 0) {
                                                        dataUtil.insertMac(atAddress, 1, 1, mac, rssi,indoorname);
                                                    } else {
                                                        dataUtil.insertStopMac(atAddress, 1, 1, mac, rssi,indoorname);
                                                    }
                                                    new_student++;
                                                    in_class_number++;
                                                    hour_in_class_number++;
                                                }
                                                //mac在普通区域或禁区已存在的情况
                                                else {
                                                    if (StopJudege == 0) {
                                                        macMap = dataUtil.getMacMap(mac, atAddress,indoorname);
                                                        if(macMap!=null){
                                                            Iterator<Map.Entry<String, Object>> entries = macMap.entrySet().iterator();
                                                            while(entries.hasNext()){
                                                                Map.Entry<String, Object> entry = entries.next();
                                                                String key = entry.getKey();
                                                                Object value = entry.getValue();
                                                            }

                                                            timeCount = new Long((latest_time.getTime() - (Long) macMap.get("beat")) / (60 * 1000)).intValue();
                                                             if (timeCount >= 1&&(Integer) macMap.get("inJudge") == 0){
                                                                macMap.put("last_in_time", (Long) macMap.get("in_time"));
                                                                macMap.put("in_time", latest_time);
                                                                macMap.put("visited_times", (Integer) macMap.get("visited_times") + 1);
                                                                in_class_number++;
                                                                hour_in_class_number++;
                                                            }

                                                            macMap.put("left_time", latest_time);
                                                            macMap.put("beat", latest_time);
                                                            macMap.put("inJudge", 1);
                                                            macMap.put("rssi", rssi);
                                                            dataUtil.refreshMacCache(mac, atAddress,macMap,indoorname);
                                                            visitMapper.updateInjudge2(1,mac,atAddress,indoorname);
                                                        }

                                                    } else {
                                                        macMap = dataUtil.getStopMacMap(mac, atAddress,indoorname);
                                                        timeCount = new Long((latest_time.getTime() - (Long) macMap.get("beat")) / (60 * 1000)).intValue();
                                                        if (timeCount >= 1&&(Integer) macMap.get("inJudge") == 0) {
                                                            macMap.put("in_time", latest_time);
                                                            macMap.put("visited_times", (Integer) macMap.get("visited_times") + 1);
                                                            in_class_number++;
                                                            hour_in_class_number++;
                                                        }
                                                        macMap.put("handleJudge", 0);
                                                        macMap.put("left_time", latest_time);
                                                        macMap.put("beat", latest_time);
                                                        macMap.put("inJudge", 1);
                                                        macMap.put("rssi", rssi);
                                                        dataUtil.refreshStopMacCache(mac,atAddress, macMap,indoorname);
                                                        stopVisitMapper.updateInjudge2(1,mac,atAddress,indoorname);
                                                    }
                                                }
                                                dataUtil.clearstu2();

                                            }
                                            else {
                                                continue;
                                            }
                                        }
                                        if(atAddress!=null){
                                            if (new_student != 0 || in_class_number != 0 || hour_in_class_number != 0 ) {
                                                classDataMapper.updateClassData(atAddress, new_student, in_class_number, hour_in_class_number,indoorname);//倒序排序更新最新的那条
                                            }
                                        }

                                        new_student = 0;
                                        in_class_number = 0;
                                        hour_in_class_number = 0;

                                        lastmachineid=nowmachineid;
                                        lastindoorname=nowindoorname;

                                        if(APcount>3){
                                            APcount = 0;
                                        }
                                    }
                                    else{
                                    }
                                }
                                else {
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


