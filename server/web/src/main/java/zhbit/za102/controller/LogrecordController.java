package zhbit.za102.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhbit.za102.bean.*;
import zhbit.za102.service.LogrecordService;
import zhbit.za102.service.MapMamageService;
import zhbit.za102.service.StopVisitService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

@RestController
public class LogrecordController {
    @Autowired
    LogrecordService logrecordService;
    @Autowired
    MapMamageService mapMamageService;

    @GetMapping("/listLogrecord")
    public Msg list(@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size,
                    @RequestParam("roledesc") String roledesc,@RequestParam("username") String username)throws Exception {  //所有用户
        try {
            List<String> c = new ArrayList<>();
            if("管理员".equals(roledesc)) {
                return logrecordService.list(start, size);
            }
            else {
                List<map_mamage> list = mapMamageService.list2(username);
                for (map_mamage map : list) {
                    List<Logrecord> b = logrecordService.list4(map.getIndoorname());
                    if(b.size()!=0){
                        for (Logrecord g : b){
                            c.add(g.getId());
                        }
                    }
                    else{
                        c.add("");
                    }
                }
                return logrecordService.list3(c,start,size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/listLogrecordSearch")
    public Msg listSearch(@RequestParam("staffdata") String staffdata,@RequestParam(value = "start",defaultValue = "1")int start,
                          @RequestParam(value = "size",defaultValue = "8")int size,
                          @RequestParam("roledesc") String roledesc,@RequestParam("username") String username)throws Exception {  //所有用户
        try {
            List<String> c = new ArrayList<>();
            if("管理员".equals(roledesc)) {
                return logrecordService.listSearch(staffdata,start, size);
            }
            else {
                List<map_mamage> list = mapMamageService.list2(username);
                for (map_mamage map : list) {
                    List<Logrecord> b = logrecordService.list5(map.getIndoorname(),staffdata);
                    for (Logrecord g : b){
                        c.add(g.getId());
                    }
                }
                return logrecordService.list3(c,start,size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }


    @DeleteMapping("/deleteLogrecord")
    public Msg delete(@RequestParam("logid") Integer logid) {
        try {
            logrecordService.delete(logid);
            return new Msg("删除操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("删除操作失败", 401);
        }
    }

    @PutMapping("/updateLogrecord")
    public Msg update(@RequestParam("logid") Integer logid, @RequestParam("id") String id, @RequestParam("changevalue") String changevalue, @RequestParam("gentime") String gentime,@RequestParam("indoorname") String indoorname) {
        try {
            Logrecord c=logrecordService.get(logid);
            c.setId(id);
            c.setChangevalue(changevalue);
            c.setGentime(gentime);
            c.setIndoorname(indoorname);
            logrecordService.update(c);
            return new Msg("修改操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("修改操作失败", 401);
        }
    }

    @PostMapping("/addLogrecord")
    public Msg add(@RequestParam("id") String id, @RequestParam("changevalue") String changevalue, @RequestParam("gentime") String gentime,@RequestParam("indoorname") String indoorname) {
        try {
            Logrecord c=new Logrecord();
            c.setId(id);
            c.setChangevalue(changevalue);
            c.setGentime(gentime);
            c.setIndoorname(indoorname);
            logrecordService.add(c);
            return new Msg("新增操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("新增操作失败", 401);
        }
    }
}
