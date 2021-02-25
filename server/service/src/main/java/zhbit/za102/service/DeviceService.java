package zhbit.za102.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zhbit.za102.bean.*;
import zhbit.za102.dao.DeviceMapper;
import zhbit.za102.dao.LogrecordMapper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "Device")
public class DeviceService {
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


    public List<Device> listbyId(String id) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIdEqualTo(id);
        return deviceMapper.selectByExample(example);
    }

    @CacheEvict(value="Device",allEntries = true)
    public void insertdevice(String deviceid,String devicetype,String devicevalue,String lasttime,String ip,Integer port,String gentime){
        deviceMapper.insertdevice(deviceid,devicetype,devicevalue,lasttime,ip,port,gentime);
    }

    public List<Device> listbyAdress(String address) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andLocationEqualTo(address).andDevicetypeEqualTo("5");  //类型5为报警器
        return deviceMapper.selectByExample(example);
    }

    public List<Device> listbyAdressLight(String address) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andLocationEqualTo(address).andDevicetypeEqualTo("6");  //类型6为灯
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
    public void monitor(String id1) throws Exception
    {

        while(true) {
            //分隔符解析
            System.out.println("进来了");

                /**3、使用receive阻塞式接收*/
                ds.receive(dp);
                String mess1=new String(dp.getData());  //转成字符串格式
                System.out.println("mess："+mess1);
                String ip=dp.getAddress().getHostAddress();  //ip地址
                int port=dp.getPort();               //端口号

                String deviceid =mess1.split(",")[0];
                String devicetype = mess1.split (",")[1];
                String devicevalue = mess1.split (",")[2].trim();
//                if(logrecordService.listbyId(id1).size()!=0){
                String kvalue=logrecordService.listbyId(id1).get(0).getChangevalue(); //根据id从logrecord01表获取对应设备的状态值
//                }

                System.out.println("按钮传来的id："+id1+"---设备id："+deviceid);
                System.out.println((kvalue.trim()).equals(devicevalue.trim()));
                if(id1.equals(deviceid))   //按钮传来的id与设备id相同时才做设备控制操作
                {
                    System.out.println("同一个设备id");
                    //判断是否有控制指令且控制指令不同于当前状态值
                    if (kvalue.equals(devicevalue))   //若真实设备状态已经改变则退出该循环进行下一个
                    {
                        System.out.println("值比较1："+kvalue);
                        System.out.println("硬件值："+devicevalue);
                        System.out.println("退出");
                        check(deviceid,devicetype,devicevalue,ip,port);
                        break;
                    }
                    else{
                        System.out.println("值比较2："+kvalue);
                        System.out.println("硬件值："+devicevalue);
                         if (kvalue.equals("0"))
                         {
                          control_value = "SWITCH0";
                         }
                         else
                         {
                          control_value = "SWITCH1";
                         }
                         try {
                             //2、提供数据，封装打包  ---将control_value值返回给指定ip地址客户端(ip是变化的，动态ip)
                             byte[] bs = control_value.getBytes();
                             DatagramPacket dp2 = new DatagramPacket(bs, bs.length,  InetAddress.getByName(ip), port);
                             /** 3、使用send发送 */
                             try {
                                 ds.send(dp2);
                                 check(deviceid,devicetype,devicevalue,ip,port);
                             } catch (IOException e) {
                                 System.out.println("发送失败： ");
                                 e.printStackTrace();
                             }
                           }
                         catch (Exception e){
                             System.out.println("Error:"+e);
                          }
                    }
                }
          }
    }

    //改数据库中设备状态
    public void check(String deviceid,String devicetype,String devicevalue,String ip,Integer port){
        Date t=new Date();
        System.out.println(t);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String dt = df.format(t);//获取当前系统时间并格式化
        // 查询发过来的设备列表中在数据库中有没有，如无则存入，如有则更新   "select * from device01 where id='" + id1 + "'"
        if(listbyId(deviceid).size()!=0)
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
            d1.setDeviceid(deviceService.listbyId(deviceid).get(0).getDeviceid());
            deviceService.update(d1);
            //deviceService.updatebyid(deviceid,devicetype,devicevalue,lasttime,ip,port,gentime);
        }
        else{
            //插入device01表
            String gentime = df.format(new Date());// 获取当前系统时间并格式化
            long now1 = System.currentTimeMillis();//获取当前时间戳
            now1=now1/1000;
            String lasttime=String.valueOf(now1);   //long型转string型
            deviceService.insertdevice(deviceid,devicetype,devicevalue,lasttime,ip,port,gentime);    //插入设备信息
        }
    }
}
