package zhbit.za102.controller;

import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import zhbit.za102.bean.ClassData;
import zhbit.za102.bean.CustomerPerHour;
import zhbit.za102.bean.InCustomerPerHour;
import zhbit.za102.bean.Msg;
import zhbit.za102.dao.ClassDataMapper;
import zhbit.za102.service.ClassDataService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ClassDataController {
    @Autowired
    ClassDataService classDataService;

    @GetMapping("/listClassData")
    public Msg list(@RequestParam(value = "start",defaultValue = "1")int start,
                    @RequestParam(value = "size",defaultValue = "8")int size)throws Exception {  //所有用户
        try {
            return classDataService.list(start, size);
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/getMainData")
    public Msg getMainData(@RequestParam("address")String address, @RequestParam("dateTime")String dateTime)throws Exception {  //所有用户
        try {
            Integer in_class_number = 0;
            Integer jmpOut = 0;
            Integer new_student = 0;
            Integer class_now_number  = 0;
            List<ClassData> shop_dataList = classDataService.getMainData(address,dateTime);
            if (shop_dataList.size()==0)
                return new Msg("请选中展示区域", 401);
            System.out.println("第一行："+shop_dataList.get(0).getClassNowNumber());
            for (ClassData shop_data:shop_dataList) {
                in_class_number+=shop_data.getInClassNumber();
                new_student+=shop_data.getNewStudent();
                jmpOut+=shop_data.getJumpOut();
            }
            class_now_number = shop_dataList.get(0).getClassNowNumber();
            Map<String,Integer> map=new HashMap<>();
            map.put("in_class_number",in_class_number);
            map.put("new_student",new_student);
            map.put("class_now_number",class_now_number);
            map.put("jmpOut",jmpOut);
            return new Msg(map);
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/getCustomerPerHour")
    public Msg getCustomerPerHour(@RequestParam("address")String address, @RequestParam("dateTime")String dateTime)throws Exception {  //所有用户
        try {
            List<CustomerPerHour> CustomerPerHourList = classDataService.getCustomerPerHour(address, dateTime);
            Collections.reverse(CustomerPerHourList);
            return new Msg(CustomerPerHourList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

    @GetMapping("/getInCustomerPerHour")
    public Msg getInCustomerPerHour(@RequestParam("address")String address, @RequestParam("dateTime")String dateTime)throws Exception {  //所有用户
        try {
            List<InCustomerPerHour> inCustomerPerHourList = classDataService.getInCustomerPerHour(address, dateTime);
            Collections.reverse(inCustomerPerHourList);
            return new Msg(inCustomerPerHourList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Msg("查询失败", 401);
        }
    }

}
