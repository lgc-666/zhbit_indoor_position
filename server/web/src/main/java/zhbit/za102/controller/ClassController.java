package zhbit.za102.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import zhbit.za102.bean.Class;

import zhbit.za102.bean.Msg;
import zhbit.za102.bean.map_mamage;
import zhbit.za102.service.ClassService;
import zhbit.za102.service.MapMamageService;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ClassController {
    @Autowired
    ClassService classService;
    @Autowired
    MapMamageService mapMamageService;

    @GetMapping("/listClassNoPagePublic")
    public Msg listPublic()throws Exception {
        try {
            return new Msg(classService.listPublic());
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/listClassNoPageStop")
    public Msg listStop()throws Exception {
        try {
            return new Msg(classService.listStop());
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/listClassNoPage")
    public Msg list(@RequestParam("indoorname") String indoorname)throws Exception {
        try {
                return new Msg(classService.list3(indoorname));
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }
    @GetMapping("/listClassNoPage2")
    public Msg list2(@RequestParam("roledesc") String roledesc,@RequestParam("username") String username)throws Exception {
        try {
            List<Class> c = new ArrayList<>();
            if("管理员".equals(roledesc)){
                return new Msg(classService.list());
            }
            else {
                List<map_mamage> list = mapMamageService.list2(username);
                for (map_mamage map : list) {
                    List<Class> b = classService.list3(map.getIndoorname());
                    for (Class g : b){
                        c.add(g);
                    }
                }
                return new Msg(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/listClass")
    public Msg list(@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size,
                    @RequestParam("roledesc") String roledesc,@RequestParam("username") String username)throws Exception {
        try {
            List<Integer> c = new ArrayList<>();
            if("管理员".equals(roledesc)) {
                return classService.list(start, size);
            }
            else {
                List<map_mamage> list = mapMamageService.list2(username);
                for (map_mamage map : list) {
                    List<Class> b = classService.list4(map.getIndoorname());
                    for (Class g : b){
                        c.add(g.getClassid());
                    }
                }
                return classService.list3(c,start,size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/listClassSearch")
    public Msg listSearch(@RequestParam("staffdata") String staffdata,@RequestParam(value = "start",defaultValue = "1")int start,
                          @RequestParam(value = "size",defaultValue = "8")int size,
                          @RequestParam("roledesc") String roledesc,@RequestParam("username") String username)throws Exception {
        try {
            List<Integer> c = new ArrayList<>();
            if("管理员".equals(roledesc)) {
                return classService.listSearch(staffdata,start, size);
            }
            else {
                List<map_mamage> list = mapMamageService.list2(username);
                for (map_mamage map : list) {
                    List<Class> b = classService.list5(map.getIndoorname(),staffdata);
                    if(b.size()!=0){
                        for (Class g : b){
                            c.add(g.getClassid());
                        }
                    }
                    else{
                        c.add(0);
                    }
                }
                return classService.list3(c,start,size);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @DeleteMapping("/deleteClass")
    public Msg delete(@RequestParam("classid") Integer classid) {
        try {
            classService.delete(classid);
            return new Msg("删除操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("删除操作失败", 401);
        }
    }

    @PutMapping("/updateClass")
    public Msg update(@RequestParam("classid") Integer classid, @RequestParam("adress") String adress, @RequestParam("x1") String x1, @RequestParam("x2") String x2,
                      @RequestParam("y1") String y1, @RequestParam("y2") String y2, @RequestParam("stopJudge") Integer stopJudge, @RequestParam("indoorname") String indoorname) {
        try {
            Class c=classService.get(classid);
            c.setAdress(adress);
            c.setX1(x1);
            c.setX2(x2);
            c.setY1(y1);
            c.setY2(y2);
            c.setIndoorname(indoorname);
            if(stopJudge!=null){
                c.setStopjudge(stopJudge);
            }
            classService.update(c);
            return new Msg("修改操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("修改操作失败", 401);
        }
    }

    @PostMapping("/addClass")
    public Msg add(@RequestParam("adress") String adress, @RequestParam("x1") String x1, @RequestParam("x2") String x2, @RequestParam("y1") String y1, @RequestParam("y2") String y2, @RequestParam("stopJudge") Integer stopJudge, @RequestParam("indoorname") String indoorname) { //更改用户权限(传数组)
        try {
            Class c=new Class();
            c.setAdress(adress);
            c.setX1(x1);
            c.setX2(x2);
            c.setY1(y1);
            c.setY2(y2);
            c.setStopjudge(stopJudge);
            c.setIndoorname(indoorname);
            classService.add(c);
            return new Msg("新增操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("新增操作失败", 401);
        }
    }
}
