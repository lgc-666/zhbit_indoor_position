package zhbit.za102.controller;

import org.joda.time.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhbit.za102.bean.*;
import zhbit.za102.service.MachineService;
import zhbit.za102.service.MapMamageService;

import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


@RestController
public class MachineController {
    @Autowired
    MachineService machineService;
    @Autowired
    MapMamageService mapMamageService;

    @GetMapping("/listmachine")
    public Msg list(@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size,
                    @RequestParam("roledesc") String roledesc,@RequestParam("username") String username)throws Exception {
        try {
            List<String> c = new ArrayList<>();
            if("管理员".equals(roledesc)) {
                return machineService.list(start, size);
            }
            else {
                List<map_mamage> list = mapMamageService.list2(username);
                for (map_mamage map : list) {
                    List<Machine> b = machineService.list4(map.getIndoorname());
                    if(b.size()!=0){
                        for (Machine g : b){
                            c.add(g.getMachineid());
                        }
                    }
                    else{
                        c.add("");
                    }
                }

                return machineService.list3(c,start,size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/listmachineSearch")
    public Msg listSearch(@RequestParam("staffdata") String staffdata,@RequestParam(value = "start",defaultValue = "1")int start,
                          @RequestParam(value = "size",defaultValue = "8")int size,
                          @RequestParam("roledesc") String roledesc,@RequestParam("username") String username)throws Exception {
        try {
            List<String> c = new ArrayList<>();
            if("管理员".equals(roledesc)) {
                return machineService.listSearch(staffdata,start, size);
            }
            else {
                List<map_mamage> list = mapMamageService.list2(username);
                for (map_mamage map : list) {
                    List<Machine> b = machineService.list5(map.getIndoorname(),staffdata);
                    for (Machine g : b){
                        c.add(g.getMachineid());
                    }
                }
                return machineService.list3(c,start,size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @DeleteMapping("/deletemachine")
    public Msg delete(@RequestParam("mid") Integer mid) {
        try {
            machineService.delete(mid);
            return new Msg("删除操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("删除操作失败", 401);
        }
    }

    @PutMapping("/updatemachine")
    public Msg update(@RequestParam("mid") Integer mid, @RequestParam("adress") String adress, @RequestParam("indoorname") String indoorname, @RequestParam("machineid") String machineid, @RequestParam("status") String status, @RequestParam("leastRssi") Integer leastRssi, @RequestParam("beat") String beat, @RequestParam("x") String x, @RequestParam("y") String y) {
        try {
            Machine c=machineService.get(mid);
            c.setAdress(adress);
            c.setMachineid(machineid);
            //String转成Date
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //Date date = sdf.parse(beat);
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = isoFormat.parse(beat);  //"2010-05-23T09:01:02"
            c.setBeat(date);
            if(leastRssi!=null){
                c.setLeastrssi(leastRssi);
            }
            c.setX(x);
            c.setY(y);
            c.setStatus(status);
            c.setIndoorname(indoorname);
            machineService.update(c);
            return new Msg("修改操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("修改操作失败", 401);
        }
    }

    @PostMapping("/addmachine")
    public Msg add(@RequestParam("adress") String adress, @RequestParam("indoorname") String indoorname, @RequestParam("machineid") String machineid, @RequestParam("status") String status, @RequestParam("leastRssi") Integer leastRssi, @RequestParam("beat") String beat, @RequestParam("x") String x, @RequestParam("y") String y) {
        try {
            Machine c=new Machine();
            c.setAdress(adress);
            c.setMachineid(machineid);
            //String转成Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(beat);
            c.setBeat(date);
            c.setLeastrssi(leastRssi);
            c.setX(x);
            c.setY(y);
            c.setStatus(status);
            c.setIndoorname(indoorname);
            machineService.add(c);
            return new Msg("新增操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("新增操作失败", 401);
        }
    }

}
