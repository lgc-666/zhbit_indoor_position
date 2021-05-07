package zhbit.za102.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import zhbit.za102.Utils.RedisUtils;
import zhbit.za102.bean.*;
import zhbit.za102.dao.DeviceMapper;
import zhbit.za102.dao.LogrecordMapper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
@CacheConfig(cacheNames = "Device")
public class DeviceService {
    @Resource
    RedisUtils redisUtil;
    @Autowired
    DeviceMapper deviceMapper;
    @Autowired
    LogrecordService logrecordService;
    @Autowired
    DeviceService deviceService;

    public DeviceService() throws SocketException {
    }

    @CacheEvict(value="Device",allEntries = true)
    public void add(Device u) {
        deviceMapper.insert(u);
    }

    @CacheEvict(value="Device",allEntries = true)
    public void delete(Integer id) { deviceMapper.deleteByPrimaryKey(id); }

    @CacheEvict(value="Device",allEntries = true)
    public void update(Device u) { deviceMapper.updateByPrimaryKeySelective(u); }

    @Cacheable(value="Device",key = "'get'+'-'+#id")
    public Device get(Integer id) {
        return deviceMapper.selectByPrimaryKey(id);
    }

    public List<Device> list() {
        DeviceExample example = new DeviceExample();
        example.setOrderByClause("deviceid desc");
        return deviceMapper.selectByExample(example);
    }

    public List<Device> list2(String staffdata) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIdEqualTo(staffdata);
        example.setOrderByClause("deviceid desc");
        return deviceMapper.selectByExample(example);
    }

    public List<Device> list5(String indoorname,String staffdata) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIdEqualTo(staffdata).andIndoornameEqualTo(indoorname);
        //example.setOrderByClause("deviceid desc");
        return deviceMapper.selectByExample(example);
    }

    public List<Device> list4(String indoorname) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIndoornameEqualTo(indoorname);
        //example.setOrderByClause("deviceid desc");
        return deviceMapper.selectByExample(example);
    }

    @Cacheable(value="Device",key = "'list'+'-'+#start+'-'+#size")
    public Msg list(int start, int size) {
        PageHelper.startPage(start, size, "deviceid desc");
        List<Device> us = list();
        PageInfo<Device> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public Msg listSearch(String staffdata,int start, int size) {
        PageHelper.startPage(start, size, "deviceid desc");
        List<Device> us = list2(staffdata);
        PageInfo<Device> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public Msg list3(List<String> us, int start, int size) {
        PageHelper.startPage(start, size);
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIdIn(us);
        List<Device> a= deviceMapper.selectByExample(example);
        PageInfo<Device> page = new PageInfo<>(a);
        return new Msg(page);
    }

    public List<Device> listbyId(String id,String indoorname) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIdEqualTo(id).andIndoornameEqualTo(indoorname);
        return deviceMapper.selectByExample(example);
    }

    public List<Device> listbyId2(String id) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIdEqualTo(id);
        return deviceMapper.selectByExample(example);
    }

    @CacheEvict(value="Device",allEntries = true)
    public void insertdevice(String deviceid,String devicetype,String devicevalue,String lasttime,String ip,Integer port,String gentime,String indoorname){
        deviceMapper.insertdevice(deviceid,devicetype,devicevalue,lasttime,ip,port,gentime,indoorname);
    }

    public List<Device> listbyAdress(String address,String indoorname) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andLocationEqualTo(address).andDevicetypeEqualTo("5").andIndoornameEqualTo(indoorname);  //类型5为报警器
        return deviceMapper.selectByExample(example);
    }

    public List<Device> listbyAdressLight(String address,String indoorname) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andLocationEqualTo(address).andDevicetypeEqualTo("6").andIndoornameEqualTo(indoorname);  //类型6为灯
        return deviceMapper.selectByExample(example);
    }

    /*硬件设备部分*/
    //节点类型：1->led  2->火焰    3->烟雾  4->人体红外  5->电机  6->wifi开关  7->A7    6。温湿度      7。温度
    /**1、建立udp socket，设置接收端口*/
    DatagramSocket ds = new DatagramSocket(3020);
    String control_value="";
    /**2、预先创建数据存放的位置，封装*/
    byte [] bbuf = new byte [1024];
    DatagramPacket dp = new DatagramPacket(bbuf,bbuf.length);
    public void monitor() throws Exception
    {
            while (true) {
                synchronized (this) {
                    try{
                        //分隔符解析
                        System.out.println("进来了");
                        /**3、使用receive阻塞式接收*/
                        ds.receive(dp);
                        String mess1 = new String(dp.getData());  //转成字符串格式
                        System.out.println("mess：" + mess1);
                        String ip = dp.getAddress().getHostAddress();  //ip地址
                        int port = dp.getPort();               //端口号

                        String deviceid = mess1.split(",")[0];
                        String devicetype = mess1.split(",")[1];
                        String devicevalue = mess1.split(",")[2].trim();
                        //String indoorname = mess1.split(",")[3].trim();  //新加的地图数据
                        if(listbyId2(deviceid).size()!=0&&listbyId2(deviceid)!=null){
                            String indoorname = listbyId2(deviceid).get(0).getIndoorname();

                            //思路：从redis拿到存储将要操作设备的list，遍历list，若若真实设备状态已经改变则将完成操作的设备从list中删除
                            List<List<String>> a = (List<List<String>>) (List) redisUtil.lGet("MachineList", 0, -1); //获取list的所有值
                            System.out.println("redis中的设备list====>size：" + a.size());
                            if (a.size() != 0 && a != null) { //缓存中有值才处理，没值则退出
                                List<String> c = a.get(0);
                                if (c.size() != 0 && c != null) {
                                    for (int i = 0; i < c.size(); i++) {  //遍历列表中的设备id
                                        if(logrecordService.listbyId(c.get(i))!=null&&logrecordService.listbyId(c.get(i)).size()!=0){
                                            String kvalue = logrecordService.listbyId(c.get(i)).get(0).getChangevalue(); //根据id从logrecord01表获取对应设备最新的操作状态值
                                            System.out.println("按钮传来的id：" + c.get(i) + "---设备id：" + deviceid);
                                            System.out.println((kvalue.trim()).equals(devicevalue.trim()));

                                            if (c.get(i).equals(deviceid))   //按钮传来的id与设备id相同时才做设备控制操作
                                            {
                                                System.out.println("同一个设备id");
                                                //判断是否有控制指令且控制指令不同于当前状态值
                                                if (kvalue.equals(devicevalue))   //若真实设备状态已经改变则退出该循环进行下一个
                                                {
                                                    System.out.println("设备id列表：" + c);
                                                    System.out.println("值比较1：" + kvalue);
                                                    System.out.println("硬件值：" + devicevalue);
                                                    check(deviceid, devicetype, devicevalue, ip, port, indoorname);
                                                    //删除redis列表中已处理完的设备id
                                                    c.remove(c.get(i));
                                                    //设置缓存
                                                    redisUtil.del("MachineList");
                                                    if (c.size() != 0) {
                                                        redisUtil.lSet("MachineList", c);
                                                    } else {
                                                        //已操作的设备已处理完，即操作设备的list为0时，退出循环
                                                        System.out.println("退出for循环");
                                                        break;
                                                    }
                                                } else {
                                                    System.out.println("值比较2：" + kvalue);
                                                    System.out.println("硬件值：" + devicevalue);
                                                    if (kvalue.equals("0")) {
                                                        control_value = c.get(i) + ",SWITCH0";
                                                    } else {
                                                        control_value = c.get(i) + ",SWITCH1";
                                                    }
                                                    try {
                                                        //2、提供数据，封装打包  ---将control_value值返回给指定ip地址客户端(ip是变化的，动态ip)
                                                        byte[] bs = control_value.getBytes();
                                                        DatagramPacket dp2 = new DatagramPacket(bs, bs.length, InetAddress.getByName(ip), port);
                                                        /** 3、使用send发送 */
                                                        try {
                                                            ds.send(dp2);
                                                            check(deviceid, devicetype, devicevalue, ip, port, indoorname);
                                                        } catch (IOException e) {
                                                            System.out.println("发送失败： ");
                                                            e.printStackTrace();
                                                        }
                                                    } catch (Exception e) {
                                                        System.out.println("Error:" + e);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            System.out.println("没有要处理的请求！！！");
                                        }
                                    }
                                    //break;//退出死循环
                                } else {
                                    //已操作的设备已处理完，即操作设备的list为0时，退出死循环
                                    //break; //退出死循环
                                }
                            } else {
                                //已操作的设备已处理完，即操作设备的list为0时，退出死循环
                                //break; //退出死循环
                                System.out.println("继续3020的死循环");
                            }
                        }
                        //ds.close();
                        }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        //System.out.println("已退出while循环！！！");
        //return;
    }

    //改数据库中设备状态
    public void check(String deviceid,String devicetype,String devicevalue,String ip,Integer port,String indoorname){
        Date t=new Date();
        System.out.println(t);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String dt = df.format(t);//获取当前系统时间并格式化
        // 查询发过来的设备列表中在数据库中有没有，如无则存入，如有则更新   "select * from device01 where id='" + id1 + "'"
        if(listbyId(deviceid,indoorname).size()!=0)
        {
            //更新device01表
            String gentime = df.format(new Date());// 获取当前系统时间并格式化
            long now1 = System.currentTimeMillis();//获取当前时间戳
            now1=now1/1000;
            String lasttime=String.valueOf(now1);   //long型转string型
            Device d1 =new Device();
            d1.setId(deviceid);
            d1.setDevicetype(devicetype);
            d1.setDevicevalue(devicevalue);
            d1.setLasttime(lasttime);
            d1.setIp(ip);
            d1.setPort(port);
            d1.setGentime(gentime);
            d1.setDeviceid(deviceService.listbyId(deviceid,indoorname).get(0).getDeviceid());
            d1.setIndoorname(indoorname);
            deviceService.update(d1);
            //deviceService.updatebyid(deviceid,devicetype,devicevalue,lasttime,ip,port,gentime);
        }
        else{
            //插入device01表
            String gentime = df.format(new Date());// 获取当前系统时间并格式化
            long now1 = System.currentTimeMillis();//获取当前时间戳
            now1=now1/1000;
            String lasttime=String.valueOf(now1);   //long型转string型
            deviceService.insertdevice(deviceid,devicetype,devicevalue,lasttime,ip,port,gentime,indoorname);    //插入设备信息
        }
    }

    @Caching(evict={
            @CacheEvict(value="ClassData",allEntries = true),
            @CacheEvict(value="Class",allEntries = true),
            @CacheEvict(value="Device",allEntries = true),
            @CacheEvict(value="Logrecord",allEntries = true),
            @CacheEvict(value="Machine",allEntries = true),
            @CacheEvict(value="MapMamage",allEntries = true),
            @CacheEvict(value="Permission", allEntries=true),
            @CacheEvict(value="RegisterApproval",allEntries = true),
            @CacheEvict(value="Role", allEntries=true),
            @CacheEvict(value="Visit",allEntries = true),
            @CacheEvict(value="StopVisit",allEntries = true),
            @CacheEvict(value="User", allEntries=true),
    })
    public void deleteAllCach(){
        System.out.println("点击刷新按钮刷新缓存！！");
    }
}
