package zhbit.za102.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhbit.za102.bean.Device;
import zhbit.za102.bean.Msg;
import zhbit.za102.service.DeviceService;

@RestController
public class DeviceController {
    @Autowired
    DeviceService deviceService;

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

}
