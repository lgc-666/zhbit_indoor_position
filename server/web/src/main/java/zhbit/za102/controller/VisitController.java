package zhbit.za102.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhbit.za102.bean.Class;
import zhbit.za102.bean.Msg;
import zhbit.za102.bean.Visit;
import zhbit.za102.service.ClassService;
import zhbit.za102.service.VisitService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@RestController
public class VisitController {
    @Autowired
    VisitService visitService;

    @GetMapping("/listVisit")
    public Msg list(@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size)throws Exception {  //所有用户
        try {
            return visitService.list(start, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/listVisitSearch")
    public Msg listSearch(@RequestParam("staffdata") String staffdata,@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size)throws Exception {  //所有用户
        try {
            return visitService.listSearch(staffdata,start, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @DeleteMapping("/deleteVisit")
    public Msg delete(@RequestParam("visitid") Integer visitid) {
        try {
            visitService.delete(visitid);
            return new Msg("删除操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("删除操作失败", 401);
        }
    }

    @PutMapping("/updateVisit")
    public Msg update(@RequestParam("visitid") Integer visitid, @RequestParam("address") String address,
                      @RequestParam("rt") String rt, @RequestParam("visited_times") Integer visited_times, @RequestParam("inJudge") Integer inJudge, @RequestParam("mac") String mac) {
        try {
            Visit c=visitService.get(visitid);
            c.setAddress(address);
            c.setVisitedTimes(visited_times);
            c.setRt(rt);
            c.setMac(mac);
            if(inJudge!=null){
                c.setInjudge(inJudge);
            }
            visitService.update(c);
            return new Msg("修改操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("修改操作失败", 401);
        }
    }

    @PostMapping("/addVisit")
    public Msg add(@RequestParam("address") String address, @RequestParam("in_time") String in_time, @RequestParam("left_time") String left_time,
                   @RequestParam("last_in_time") String last_in_time,
                   @RequestParam("rt") String rt, @RequestParam("visited_times") Integer visited_times, @RequestParam("inJudge") Integer inJudge, @RequestParam("mac") String mac) {
        try {
            Visit c=new Visit();
            //String转成Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //Date date = sdf.parse(beat);
            //获取当前时间
            Calendar d=Calendar.getInstance(TimeZone.getTimeZone("GMT:+08:00"));
            c.setInTime(sdf.parse(in_time));
            c.setBeat(d.getTime());
            c.setLastInTime(sdf.parse(last_in_time));
            c.setLeftTime(sdf.parse(left_time));
            c.setAddress(address);
            c.setVisitedTimes(visited_times);
            c.setRt(rt);
            c.setMac(mac);
            c.setInjudge(inJudge);
            visitService.add(c);
            return new Msg("新增操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("新增操作失败", 401);
        }
    }

    @GetMapping("/sortVisit")
    public Msg sortVisit(@RequestParam("dateTime")String dateTime,@RequestParam("address") String address)throws Exception {  //所有用户
        try {
            if(visitService.getSumVisit(dateTime, address)!=null){
                return new Msg(visitService.getSumVisit(dateTime, address).getSum_visited_times());
            }
            else {
                return new Msg(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/sortStoptime")
    public Msg sortStoptime(@RequestParam("dateTime")String dateTime,@RequestParam("address") String address)throws Exception {  //所有用户
        try {
            if(visitService.getSumStoptime(dateTime, address)!=null){
                return new Msg(visitService.getSumStoptime(dateTime, address).getStoptime());
            }
            else {
                return new Msg(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/sortNow")
    public Msg sortNow(@RequestParam("address") String address)throws Exception {  //所有用户
        try {
                return new Msg(visitService.getsortNow(address));
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }


}
