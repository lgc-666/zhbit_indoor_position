package zhbit.za102.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhbit.za102.bean.Location;
import zhbit.za102.bean.Msg;
import zhbit.za102.bean.map_mamage;
import zhbit.za102.service.LocationService;
import zhbit.za102.service.MapMamageService;


import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class LocationController {
    @Autowired
    LocationService locationService;
    @Autowired
    MapMamageService mapMamageService;

    @GetMapping("/listLocation")
    public Msg list(@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size)throws Exception {
        try {
            return locationService.list(start, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/getDBlocation")
    public Msg getDBlocation(){
        try {
            return new Msg(locationService.list());
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/getDBlocationNotRepeat")
    public Msg getDBlocation2(@RequestParam("indoorname")String indoorname,@RequestParam("start")String start,@RequestParam("end")String end){  //所有用户
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sdf.parse(start);
            Date date2 = sdf.parse(end);
            List<String> listmac=locationService.searchLocationMac(indoorname);
            List<Location> listlocation = new ArrayList<>();
            for(String mac:listmac){
                List<Location> a= locationService.searchLocationleatMac(mac,date1,date2);
                if(a.size()!=0){
                    listlocation.add(a.get(0));
                }
            }
            return new Msg(listlocation);
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/listByMac")
    public Msg listByMac(@RequestParam("mac") String mac,@RequestParam("indoorname")String indoorname,@RequestParam("start")String start,@RequestParam("end")String end){  //所有用户
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sdf.parse(start);
            Date date2 = sdf.parse(end);
            return new Msg(locationService.listByMac2(mac,indoorname,date1,date2));
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/mapByMac")
    public Msg mapByMac(@RequestParam("mac") String mac,@RequestParam("start")String start,@RequestParam("end")String end){  //所有用户
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = sdf.parse(start);
            Date date2 = sdf.parse(end);
            List<Location> a = locationService.mapByMac(mac,date1,date2);
            List<map_mamage> dataList = new ArrayList<>();
            if(a.size()!=0){
                for(int i=0;i<a.size();i++){
                    List<map_mamage> b = mapMamageService.listSearchByIndoorname(a.get(i).getIndoorname());
                    if(b.size()!=0){
                        map_mamage c = b.get(0);
                        dataList.add(c);
                    }
                }
                return new Msg(dataList);
            }
            else{
                return new Msg("查询失败，该时间段内暂无该mac的记录", 401);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败，该时间段内暂无该mac的记录", 401);
        }
    }
}
