package zhbit.za102.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import zhbit.za102.Utils.RedisUtils;
import zhbit.za102.bean.*;
import zhbit.za102.service.DeviceService;
import zhbit.za102.service.LogrecordService;
import zhbit.za102.service.MapMamageService;
import zhbit.za102.service.StopVisitService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class StopVisitController {
    @Resource
    RedisUtils redisUtil;
    @Autowired
    StopVisitService stopvisitService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    LogrecordService logrecordService;
    @Autowired
    MapMamageService mapMamageService;

    @GetMapping("/listStopVisit")
    public Msg list(@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size,
                    @RequestParam("roledesc") String roledesc,@RequestParam("username") String username)throws Exception {
        try {
            List<Integer> c = new ArrayList<>();
            //已改
            if("管理员".equals(roledesc)) {
                return stopvisitService.list(start, size);
            }
            else {
                List<map_mamage> list = mapMamageService.list2(username);
                for (map_mamage map : list) {
                    List<StopVisit> b = stopvisitService.list4(map.getIndoorname());
                    if(b.size()!=0){
                        for (StopVisit g : b){
                            c.add(g.getStopVisitId());
                        }
                    }
                    else{
                        c.add(0);
                    }
                }
                return stopvisitService.list3(c,start,size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/listStopVisitSearch")
    public Msg listSearch(@RequestParam("staffdata") String staffdata,@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size)throws Exception {
        try {
            return stopvisitService.listSearch(staffdata, start, size);
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
                      @RequestParam("rt") String rt, @RequestParam("visited_times") Integer visited_times, @RequestParam("inJudge") Integer inJudge,
                      @RequestParam("mac") String mac, @RequestParam("indoorname") String indoorname) {
        try {
            StopVisit c=stopvisitService.get(stop_visit_id);
            c.setAddress(address);
            c.setVisitedTimes(visited_times);
            c.setRt(rt);
            c.setMac(mac);
            c.setHandlejudge(handleJudge);
            c.setIndoorname(indoorname);
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
                   @RequestParam("handleJudge") Integer handleJudge, @RequestParam("indoorname") String indoorname,
                   @RequestParam("rt") String rt, @RequestParam("visited_times") Integer visited_times, @RequestParam("inJudge") Integer inJudge, @RequestParam("mac") String mac) {
        try {
            StopVisit c=new StopVisit();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
            c.setIndoorname(indoorname);
            stopvisitService.add(c);
            return new Msg("新增操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("新增操作失败", 401);
        }
    }

    @PutMapping("/doStopVisit")
    public Msg doStopVisit(@RequestParam("stop_visit_id") Integer stop_visit_id,@RequestParam("address") String address,@RequestParam("indoorname") String indoorname) {
        try {
            StopVisit u= new StopVisit();
            u.setHandlejudge(1);
            u.setStopVisitId(stop_visit_id);
            u.setIndoorname(indoorname);
            stopvisitService.update(u);
            List<Device> devices = deviceService.listbyAdress(address,indoorname);
            for(Device d:devices){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dt = df.format(new Date());
                logrecordService.addchange(d.getId(),"0",dt,indoorname);
            }
            return new Msg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("操作失败", 401);
        }
    }

}
