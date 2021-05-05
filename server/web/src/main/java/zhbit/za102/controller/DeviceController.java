package zhbit.za102.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import zhbit.za102.Utils.DataUtil;
import zhbit.za102.Utils.RedisUtils;
import zhbit.za102.bean.Device;
import zhbit.za102.bean.Logrecord;
import zhbit.za102.bean.Msg;
import zhbit.za102.service.DeviceService;
import zhbit.za102.service.LogrecordService;

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

@RestController
public class DeviceController {
    @Autowired
    DeviceService deviceService;
    @Autowired
    LogrecordService logrecordService;
    @Resource
    RedisUtils redisUtil;

    public DeviceController() throws SocketException {
    }

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

    @GetMapping("/listDeviceSearch")
    public Msg listSearch(@RequestParam("staffdata") String staffdata,@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size)throws Exception {  //所有用户
        try {
            return deviceService.listSearch(staffdata,start, size);
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
                      @RequestParam("lasttime") String lasttime,@RequestParam("gentime") String gentime, @RequestParam("ip") String ip,
                      @RequestParam("indoorname") String indoorname) {
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
            c.setIndoorname(indoorname);
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
                   @RequestParam("ip") String ip,@RequestParam("port") Integer port,@RequestParam("indoorname") String indoorname) {
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
            c.setIndoorname(indoorname);
            deviceService.add(c);
            return new Msg("新增操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("新增操作失败", 401);
        }
    }

    @PutMapping("/updateStatus")
    public Msg updateStatus(@RequestParam("status") String status,@RequestParam("id") String id,@RequestParam("indoorname") String indoorname) {
        try {
            System.out.println("设备操控");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String dt = df.format(new Date());//获取当前系统时间并格式化
            System.out.println("id-stataus-dt的值："+id+"-"+status+"-"+dt);
            logrecordService.addchange(id,status,dt,indoorname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Msg();
    }

    @PutMapping("/updateStatus2")
    public Msg updateStatus2(@RequestParam("status") String status) {
        try {
            System.out.println("stataus的值："+status);
            //通过在redis中存值判断是否进行自动控制
                redisUtil.del("control");
                redisUtil.set("control",status);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Msg();
    }

    @PutMapping("/getcontrol")
    public Msg getcontrol() {
        try {
           String u = (String) redisUtil.get("control");
            return new Msg(u);
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @DeleteMapping("/deleteAllCach")
    public Msg deleteAllCach() {
        try {
            deviceService.deleteAllCach();
            return new Msg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("操作失败", 401);
        }
    }
}
