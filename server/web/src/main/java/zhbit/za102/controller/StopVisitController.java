package zhbit.za102.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhbit.za102.bean.Msg;
import zhbit.za102.bean.StopVisit;
import zhbit.za102.bean.Visit;
import zhbit.za102.service.StopVisitService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

@RestController
public class StopVisitController {
    @Autowired
    StopVisitService stopvisitService;

    @GetMapping("/listStopVisit")
    public Msg list(@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size)throws Exception {  //所有用户
        try {
            return stopvisitService.list(start, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @DeleteMapping("/deleteStopVisit")
    public Msg delete(@RequestParam("stop_visit_id") Integer stop_visit_id) {
        try {
            stopvisitService.delete(stop_visit_id);
            return new Msg("删除操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("删除操作失败", 401);
        }
    }

    @PutMapping("/updateStopVisit")
    public Msg update(@RequestParam("stop_visit_id") Integer stop_visit_id, @RequestParam("address") String address,@RequestParam("handleJudge") Integer handleJudge,
                      @RequestParam("rt") String rt, @RequestParam("visited_times") Integer visited_times, @RequestParam("inJudge") Integer inJudge, @RequestParam("mac") String mac) {
        try {
            StopVisit c=stopvisitService.get(stop_visit_id);
            c.setAddress(address);
            c.setVisitedTimes(visited_times);
            c.setRt(rt);
            c.setMac(mac);
            c.setHandlejudge(handleJudge);
            if(inJudge!=null){
                c.setInjudge(inJudge);
            }
            stopvisitService.update(c);
            return new Msg("修改操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("修改操作失败", 401);
        }
    }

    @PostMapping("/addStopVisit")
    public Msg add(@RequestParam("address") String address, @RequestParam("in_time") String in_time, @RequestParam("left_time") String left_time,
                   @RequestParam("handleJudge") Integer handleJudge,
                   @RequestParam("rt") String rt, @RequestParam("visited_times") Integer visited_times, @RequestParam("inJudge") Integer inJudge, @RequestParam("mac") String mac) {
        try {
            StopVisit c=new StopVisit();
            //String转成Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            //Date date = sdf.parse(beat);
            //获取当前时间
            Calendar d=Calendar.getInstance(TimeZone.getTimeZone("GMT:+08:00"));
            c.setInTime(sdf.parse(in_time));
            c.setBeat(d.getTime());
            c.setHandlejudge(handleJudge);
            c.setLeftTime(sdf.parse(left_time));
            c.setAddress(address);
            c.setVisitedTimes(visited_times);
            c.setRt(rt);
            c.setMac(mac);
            c.setInjudge(inJudge);
            stopvisitService.add(c);
            return new Msg("新增操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("新增操作失败", 401);
        }
    }

}
