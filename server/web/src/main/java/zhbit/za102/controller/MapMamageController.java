package zhbit.za102.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhbit.za102.bean.Msg;
import zhbit.za102.bean.map_mamage;
import zhbit.za102.service.MapMamageService;

@RestController
public class MapMamageController {
    @Autowired
    MapMamageService mapMamageService;


    @GetMapping("/listMapMamageSearchByIndoorname")
    public Msg listSearchIndoorname(@RequestParam("staffdata") String staffdata)throws Exception {  //所有用户
        try {
            return  new Msg(mapMamageService.listSearchByIndoorname(staffdata).get(0));
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/listMapMamageSearch")
    public Msg listSearch(@RequestParam("staffdata") String staffdata,@RequestParam(value = "start",defaultValue = "1")int start,
                          @RequestParam(value = "size",defaultValue = "8")int size)throws Exception {  //所有用户
        try {
            return  mapMamageService.listSearch(staffdata, start, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/listMapMamageNoPage")
    public Msg list()throws Exception {  //所有用户
        try {
            return new Msg(mapMamageService.list());
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }


    @GetMapping("/listMapMamage")
    public Msg list(@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size)throws Exception {  //所有用户
        try {
            return mapMamageService.list(start, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @DeleteMapping("/deleteMapMamage")
    public Msg delete(@RequestParam("id") Integer id) {
        try {
            mapMamageService.delete(id);
            return new Msg("删除操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("删除操作失败", 401);
        }
    }

    @PutMapping("/updateMapMamage")
    public Msg update(@RequestParam("id") Integer id, @RequestParam("fmapID") String fmapID,@RequestParam("indoorname") String indoorname,
                      @RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude, @RequestParam("charge") String charge) {
        try {
            map_mamage c=mapMamageService.get(id);
            c.setFmapid(fmapID);
            c.setIndoorname(indoorname);
            c.setLongitude(longitude);
            c.setLatitude(latitude);
            c.setCharge(charge);
            mapMamageService.update(c);
            return new Msg("修改操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("修改操作失败", 401);
        }
    }

    @PostMapping("/addMapMamage")
    public Msg add(@RequestParam("fmapID") String fmapID,@RequestParam("indoorname") String indoorname,
                      @RequestParam("longitude") String longitude, @RequestParam("latitude") String latitude, @RequestParam("charge") String charge) {
        try {
            map_mamage c=new map_mamage();
            c.setFmapid(fmapID);
            c.setIndoorname(indoorname);
            c.setLongitude(longitude);
            c.setLatitude(latitude);
            c.setCharge(charge);
            mapMamageService.add(c);
            return new Msg("新增操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("新增操作失败", 401);
        }
    }

}
