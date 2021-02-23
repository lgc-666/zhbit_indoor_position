package zhbit.za102.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhbit.za102.Utils.DataUtil;
import zhbit.za102.Utils.RedisUtils;
import zhbit.za102.bean.Device;
import zhbit.za102.bean.Logrecord;
import zhbit.za102.bean.Msg;
import zhbit.za102.service.DeviceService;
import zhbit.za102.service.LogrecordService;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class DeviceController {
    @Autowired
    DeviceService deviceService;
    @Autowired
    LogrecordService logrecordService;
    @Resource
    RedisUtils redisUtil;

    @GetMapping("/listDevice")
    public Msg list(@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size)throws Exception {  //所有用户
        try {
            return deviceService.list(start, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @DeleteMapping("/deleteDevice")
    public Msg delete(@RequestParam("deviceid") Integer deviceid) {
        try {
            deviceService.delete(deviceid);
            return new Msg("删除操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("删除操作失败", 401);
        }
    }

    @PutMapping("/updateDevice")
    public Msg update(@RequestParam("deviceid") Integer deviceid, @RequestParam("devicename") String devicename,@RequestParam("id") String id,
                      @RequestParam("devicetype") String devicetype, @RequestParam("devicevalue") String devicevalue, @RequestParam("location") String location,
                      @RequestParam("lasttime") String lasttime,@RequestParam("gentime") String gentime, @RequestParam("ip") String ip) {
        try {
            Device c=deviceService.get(deviceid);
            c.setDevicename(devicename);
            c.setDevicetype(devicetype);
            c.setDevicevalue(devicevalue);
            c.setGentime(gentime);
            c.setLasttime(lasttime);
            c.setLocation(location);
            c.setIp(ip);
            c.setId(id);
            deviceService.update(c);
            return new Msg("修改操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("修改操作失败", 401);
        }
    }

    @PostMapping("/addDevice")
    public Msg add(@RequestParam("deviceid") Integer deviceid, @RequestParam("devicename") String devicename,@RequestParam("id") String id,
                   @RequestParam("devicetype") String devicetype, @RequestParam("devicevalue") String devicevalue, @RequestParam("location") String location,
                   @RequestParam("lasttime") String lasttime,@RequestParam("gentime") String gentime,@RequestParam("owner") String owner,
                   @RequestParam("ip") String ip,@RequestParam("port") Integer port) {
            try {
            Device c=new Device();
            c.setDevicename(devicename);
            c.setDevicetype(devicetype);
            c.setDevicevalue(devicevalue);
            c.setGentime(gentime);
            c.setLasttime(lasttime);
            c.setLocation(location);
            c.setIp(ip);
            c.setId(id);
            c.setPort(port);
            c.setOwner(owner);
            deviceService.add(c);
            return new Msg("新增操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("新增操作失败", 401);
        }
    }

    @PutMapping("/updateStatus")
    public Msg updateStatus(@RequestParam("status") String status,@RequestParam("id") String id) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String dt = df.format(new Date());//获取当前系统时间并格式化
            logrecordService.addchange(id,status,dt);
            monitor(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Msg();
    }

/*硬件设备部分*/
    public void monitor(String id1) throws Exception
    {
        /**1、建立udp socket，设置接收端口*/
        DatagramSocket ds = new DatagramSocket(3020);
        String control_value="";
        /**2、预先创建数据存放的位置，封装*/
        byte [] bbuf = new byte [1024];
        DatagramPacket dp = new DatagramPacket(bbuf,bbuf.length);
        while(true) {
            //分隔符解析
            System.out.println("进来了");
            try {
                /**3、使用receive阻塞式接收*/
                ds.receive(dp);
                String mess1=new String(dp.getData());  //转成字符串格式
                String ip=dp.getAddress().getHostAddress();  //ip地址
                int port=dp.getPort();               //端口号

                String deviceid =mess1.split(",")[0];
                String devicetype = mess1.split (",")[1];
                String devicevalue = mess1.split (",")[2];
                String kvalue=logrecordService.listbyId(id1).get(0).getChangevalue(); //根据id从logrecord01表获取对应设备的状态值
                Date t=new Date();
                System.out.println(t);
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String dt = df.format(t);//获取当前系统时间并格式化

                if(id1.equals(deviceid))   //按钮传来的id与设备id相同时才做设备控制操作
                {
                    //判断是否有控制指令且控制指令不同于当前状态值
                    if (kvalue != "99" && kvalue != devicevalue)
                    {
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
                            } catch (IOException e) {
                                System.out.println("发送失败： ");
                                e.printStackTrace();
                            }
                        }
                        catch (Exception e){
                            System.out.println("Error:"+e);
                        }
                    }
                    // 查询有无设备信息，如无则存入，如有则更新   "select * from device01 where id='" + id1 + "'"
                    if(deviceService.listbyId(deviceid).size()!=0)
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
                //节点类型：1->led  2->火焰    3->烟雾  4->人体红外  5->电机  6->wifi开关  7->A7    6。温湿度      7。温度
                //如果是wifi开关存入设备详细历史记录（因为inser_device中没有对device_type进行更新，所以6是定值，默认对type=6进行观察，现有设备也仅有6的）

            }
            catch(Exception e){
                System.out.println("Error:"+ e);
            }
        }
    }
}
