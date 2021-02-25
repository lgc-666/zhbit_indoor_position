package zhbit.za102.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhbit.za102.bean.Device;
import zhbit.za102.bean.Msg;
import zhbit.za102.bean.StopVisit;
import zhbit.za102.bean.Visit;
import zhbit.za102.service.DeviceService;
import zhbit.za102.service.LogrecordService;
import zhbit.za102.service.StopVisitService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@RestController
public class StopVisitController {
    @Autowired
    StopVisitService stopvisitService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    LogrecordService logrecordService;

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

    @GetMapping("/listStopVisitSearch")
    public Msg listSearch(@RequestParam("staffdata") String staffdata,@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size)throws Exception {  //所有用户
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

    @PutMapping("/doStopVisit")
    public Msg doStopVisit(@RequestParam("stop_visit_id") Integer stop_visit_id,@RequestParam("address") String address) {
        try {
            System.out.println("已处理关闭报警器");
            //更新禁止区域处理状态
            StopVisit u= new StopVisit();
            u.setHandlejudge(1);
            u.setStopVisitId(stop_visit_id);
            stopvisitService.update(u);
            //关闭禁止区域的报警器（报警器类型是5）
            List<Device> devices = deviceService.listbyAdress(address);
            for(Device d:devices){
                System.out.println("设备的id："+d.getId());
                System.out.println("设备操控");
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String dt = df.format(new Date());//获取当前系统时间并格式化
                System.out.println("id-stataus-dt的值："+d.getId()+"-"+dt);
                logrecordService.addchange(d.getId(),"0",dt);
                deviceService.monitor(d.getId());
            }
            return new Msg("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("操作失败", 401);
        }
    }

}
