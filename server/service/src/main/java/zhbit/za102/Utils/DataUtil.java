package zhbit.za102.Utils;

import Jama.Matrix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import zhbit.za102.bean.*;
import zhbit.za102.bean.Class;
import zhbit.za102.service.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.SocketException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class DataUtil {
    @Resource
    RedisUtils redisUtil;
    @Autowired
    DeviceService deviceService;
    @Autowired
    LogrecordService logrecordService;
    @Autowired
    VisitService visitService;
    @Autowired
    StopVisitService stopVisitService;
    @Autowired
    ClassDataService classDataService;
    @Autowired
    ClassService classService;
    @Autowired
    MachineService machineService;
    @Autowired
    LocationService locationService;

    Map<String,Object> macMap;
    Map<String,Object> submacMap;

    public List<Map<String,Object>> stu2=new ArrayList();
    public List<List<Map<String,Object>>> stu3=new ArrayList();

    double totalWeight = 0;

    public List<List<Map<String, Object>>> getStu3() {
        return stu3;
    }

    Timestamp dt;
    /**
     * 检查Mac是否已经存在过
     * @param mac
     * @return
     */
    public  boolean checkExist(String mac,String address,String indoorname){
        if ((redisUtil.hget("visit",mac+'-'+address+'-'+indoorname)!=null)||(redisUtil.hget("stopvisit",mac+'-'+address+'-'+indoorname)!=null)){
            return true;
        }
        else{
            List<Visit> visits = visitService.findvisitByMac(mac,address);
            List<StopVisit> stopvisits = stopVisitService.findstopVisitByMac(mac);
            if (visits.isEmpty()||stopvisits.isEmpty()){
                return false;
            }
            else{
                if(!visits.isEmpty()){
                    for(int i=0; i<visits.size();i++)
                    {
                        cacheMac(visits.get(i).getAddress(),visits.get(i).getInjudge(),visits.get(i).getInTime(),visits.get(i).getLeftTime(),visits.get(i).getRt(),visits.get(i).getVisitedTimes(),visits.get(i).getBeat(),visits.get(i).getLastInTime(),visits.get(i).getMac(),visits.get(i).getRssi(),visits.get(i).getIndoorname());
                    }
                }
                if(!stopvisits.isEmpty()) {
                    for (int i = 0; i < stopvisits.size(); i++) {
                        cacheStopMac(stopvisits.get(i).getAddress(), stopvisits.get(i).getInjudge(), stopvisits.get(i).getInTime(), stopvisits.get(i).getLeftTime(), stopvisits.get(i).getRt(), stopvisits.get(i).getVisitedTimes(), stopvisits.get(i).getBeat(), stopvisits.get(i).getHandlejudge(), stopvisits.get(i).getMac(), stopvisits.get(i).getRssi(),stopvisits.get(i).getIndoorname());
                    }
                }
                return true;
            }
        }
    }

    /**存AP发来的mac值到缓存**/
    public void cacheMac(String address, Integer inJudge, Date in_time, Date left_time, String rt, Integer visited_times, Date beat, Date last_in_time, String mac, Integer rssi, String indoorname){
        macMap = new HashMap<>();
        submacMap = new HashMap<>();
        submacMap.put("mac",mac);
        submacMap.put("rssi",rssi);
        submacMap.put("address",address);
        submacMap.put("inJudge",inJudge);
        submacMap.put("in_time",in_time);
        submacMap.put("left_time",left_time);
        submacMap.put("rt",rt);
        submacMap.put("visited_times",visited_times);
        submacMap.put("beat",beat);
        submacMap.put("last_in_time",last_in_time);
        submacMap.put("indoorname",indoorname);
        redisUtil.hset("visit",mac+'-'+address+'-'+indoorname,submacMap);
    }
    public void cacheStopMac(String address, Integer inJudge, Date in_time, Date left_time, String rt, Integer visited_times, Date beat, Integer handleJudge, String mac, Integer rssi, String indoorname){
        macMap = new HashMap<>();
        submacMap = new HashMap<>();
        submacMap.put("mac",mac);
        submacMap.put("rssi",rssi);
        submacMap.put("address",address);
        submacMap.put("inJudge",inJudge);
        submacMap.put("in_time",in_time);
        submacMap.put("left_time",left_time);
        submacMap.put("rt",rt);
        submacMap.put("visited_times",visited_times);
        submacMap.put("beat",beat);
        submacMap.put("handleJudge",handleJudge);
        submacMap.put("indoorname",indoorname);
        redisUtil.hset("stopvisit",mac+'-'+address+'-'+indoorname,submacMap);
    }

    //向普通区域表插数值
    public boolean insertMac(String address, Integer inJudge, Integer visited_times, String mac, Integer rssi,String indoorname){
        dt = new Timestamp(System.currentTimeMillis());
        Visit u = new Visit();
        u.setMac(mac);
        u.setRssi(rssi);
        u.setAddress(address);
        u.setInjudge(inJudge);
        u.setInTime(dt);
        u.setLeftTime(dt);
        u.setRt("0");
        u.setVisitedTimes(visited_times);
        u.setBeat(dt);
        u.setLastInTime(dt);
        u.setIndoorname(indoorname);
        visitService.add(u);
        cacheMac(address,inJudge,dt,dt,"0",visited_times,dt,dt,mac,rssi,indoorname);
        return true;
    }
    //向禁止区域表插数值
    public boolean insertStopMac(String address, Integer inJudge, Integer visited_times, String mac, Integer rssi,String indoorname) throws Exception {
        dt = new Timestamp(System.currentTimeMillis());
        StopVisit u = new StopVisit();
        u.setMac(mac);
        u.setRssi(rssi);
        u.setAddress(address);
        u.setInjudge(inJudge);
        u.setInTime(dt);
        u.setLeftTime(dt);
        u.setRt("0");
        u.setVisitedTimes(visited_times);
        u.setBeat(dt);
        u.setHandlejudge(0);
        u.setIndoorname(indoorname);
        stopVisitService.add(u);
        cacheStopMac(address,inJudge,dt,dt,"0",visited_times,dt,0,mac,rssi,indoorname);

        System.out.println("非法进入开启报警器");
        List<Device> devices = deviceService.listbyAdress(address,indoorname);
        if(devices.size()!=0){
            for(Device d:devices){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dt = df.format(new Date());
                logrecordService.addchange(d.getId(),"1",dt,indoorname);
            }
        }
        return true;
    }

    //初始化区域数据统计表
    public boolean initClassData(){
        if (classDataService.selectWithin1hour()==0){
            insertClassData();
            return true;
        }else{
            Calendar c = Calendar.getInstance();
            Integer hours = c.get(Calendar.HOUR_OF_DAY);
            List<String> addressList = classService.findAllClass();
            classDataService.updateWithin1hour(hours,addressList.size());
        }
        return true;
    }

    public boolean insertClassData(){
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        List<Class> addressList = classService.findAllClassBean();
        for (Class a:addressList) {
            classDataService.insertClassData(a.getAdress(),hours,a.getIndoorname());
        }
        return true;
    }

    //获取普通区域的相应mac的用户信息
    public Map<String,Object> getMacMap(String mac,String address,String indoorname)
    {
        if (redisUtil.hget("visit",mac+'-'+address+'-'+indoorname)!=null){
            return (Map)redisUtil.hget("visit",mac+'-'+address+'-'+indoorname);
        }

        else {
            List<Visit> visits = visitService.findvisitByMac(mac,address);
            for (int i = 0; i < visits.size(); i++) {
                cacheMac(visits.get(i).getAddress(), visits.get(i).getInjudge(), visits.get(i).getInTime(), visits.get(i).getLeftTime(), visits.get(i).getRt(), visits.get(i).getVisitedTimes(), visits.get(i).getBeat(), visits.get(i).getLastInTime(), visits.get(i).getMac(), visits.get(i).getRssi(),visits.get(i).getIndoorname());
            }
            return (Map)redisUtil.hget("visit",mac+'-'+address+'-'+indoorname);
        }
    }
    //获取禁止区域的相应mac的用户信息
    public Map<String,Object> getStopMacMap(String mac,String address,String indoorname)
    {
        if (redisUtil.hget("stopvisit",mac+'-'+address+'-'+indoorname)!=null)
            return (Map)redisUtil.hget("stopvisit",mac+'-'+address+'-'+indoorname);
        else{
            List<StopVisit> stopvisits = stopVisitService.findstopVisitByMac(mac);
            for (int i = 0; i < stopvisits.size(); i++) {
                cacheStopMac(stopvisits.get(i).getAddress(), stopvisits.get(i).getInjudge(), stopvisits.get(i).getInTime(), stopvisits.get(i).getLeftTime(), stopvisits.get(i).getRt(), stopvisits.get(i).getVisitedTimes(), stopvisits.get(i).getBeat(), stopvisits.get(i).getHandlejudge(), stopvisits.get(i).getMac(), stopvisits.get(i).getRssi(),stopvisits.get(i).getIndoorname());
            }
            return (Map)redisUtil.hget("stopvisit",mac+'-'+address+'-'+indoorname);
        }
    }

    //更新普通区域的缓存信息
    public void refreshMacCache(String mac,String address,Map macMap,String indoorname){
        redisUtil.hset("visit",mac+'-'+address+'-'+indoorname,macMap);
    }
    //更新禁止区域的缓存信息
    public void refreshStopMacCache(String mac,String address,Map stopmacMap,String indoorname){
        redisUtil.hset("stopvisit",mac+'-'+address+'-'+indoorname,stopmacMap);
    }

    //设备缓存初始化
    public void refreshMachineCache(){
        List<Machine> machineList = machineService.listAll();
        Map<String,Object> machineMap = new HashMap<>();
        for (Machine machine:machineList) {
            Map<String,Object> subMachineMap = new HashMap<>();
            subMachineMap.put("machineId",machine.getMachineid());
            subMachineMap.put("address",machine.getAdress());
            subMachineMap.put("status",machine.getStatus());
            subMachineMap.put("leastRssi",machine.getLeastrssi());
            subMachineMap.put("beat",machine.getBeat());
            subMachineMap.put("x",machine.getX());
            subMachineMap.put("y",machine.getY());
            subMachineMap.put("indoorname",machine.getIndoorname());
            machineMap.put(machine.getMachineid(),subMachineMap);
        }
        redisUtil.del("machineAP");
        redisUtil.hmset("machineAP",machineMap);
        System.out.println(redisUtil.hget("machineAP","101"));
    }

    public void get(String machineid){
        System.out.println("测试："+redisUtil.hget("machineAP",machineid));
    }

    public Object getmachineAP(String machineid){
        return redisUtil.hget("machineAP",machineid);
    }


    public void refreshMachineCacheBeat(String machineId){
        System.out.println(machineId);
        System.out.println(getmachineAP(machineId));
        Map<String,Object> subMachineMap = (Map<String,Object>)getmachineAP(machineId);
        System.out.println(subMachineMap);
        subMachineMap.put("beat",new Timestamp(System.currentTimeMillis()));
        redisUtil.hset("machineAP",machineId,subMachineMap);
    }

    public int getDayDiffer(Date startDate, Date endDate) throws ParseException {
        //判断是否跨年
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        String startYear = yearFormat.format(startDate);
        String endYear = yearFormat.format(endDate);
        if (startYear.equals(endYear)) {
            /*  使用Calendar跨年的情况会出现问题    */
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            int startDay = calendar.get(Calendar.DAY_OF_YEAR);
            calendar.setTime(endDate);
            int endDay = calendar.get(Calendar.DAY_OF_YEAR);
            return endDay - startDay;
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            long startDateTime = dateFormat.parse(dateFormat.format(startDate)).getTime();
            long endDateTime = dateFormat.parse(dateFormat.format(endDate)).getTime();
            return (int) ((endDateTime - startDateTime) / (1000 * 3600 * 24));
        }
    }

    /**
     * 排列组合
     */
    public void reSort(List<Map<String,Object>> shu, int targ, int has, int cur) {
            if(has == targ) {
                List<Map<String,Object>>stu4=new ArrayList();
                System.out.println(stu2);
                stu4.addAll(stu2);
                stu3.add(stu4);
                return;
            }

            for(int i=cur;i<shu.size();i++) {
                if(!stu2.contains(shu.get(i))) {
                    stu2.add(shu.get(i));
                    reSort(shu, targ, has+1, i);
                    stu2.remove(stu2.size()-1);
                }
            }
    }

    public void clearstu2(){
        stu2.clear();
        stu3.clear();
        return;
    }

    /**利用RSSI得到设备距AP的距离(单位：米)**/
    public double GetDisFromRSSI(Integer rssi)
    {
        int A=45;
        double n=3.5;
        double rawDis = 0.0;
        double power = (-A - rssi) / (10 * n);
        double height = 5.0;
        rawDis = Math.pow(10, power);

        if(rawDis<5.0){
            rawDis=5.0;
        }
        double d=Math.sqrt(Math.pow(rawDis, 2) - Math.pow(height, 2));
        d=Double.valueOf(String.format("%.1f", d));
        return d;
    }

    /**基于rssi的加权定位算法实现多个AP定位坐标求解**/
    public Map<String, Double> CaculateByAPList(List<Map<String,Object>> apArray,String mac){
        Map<String, Double> point=new HashMap<>();
        double[][] a_array = new double[2][2];
        double[][] b_array = new double[2][1];

        double[] distanceArray = new double[3];
        for(int i = 0; i< 3; i++)
        {
            String id = apArray.get(i).get("machineId").toString();
            Map<String,Object> map =(Map<String,Object>)redisUtil.hget(mac,id);
            Integer rssivalue = (Integer) map.get("rssi");
            distanceArray[i] = GetDisFromRSSI(rssivalue);
        }

        //系数矩阵A初始化
        for (int i = 0; i<2; i++)
        {
               a_array[i][0] = 2 * ((Double.valueOf(apArray.get(i).get("x").toString())) -(Double.valueOf(apArray.get(2).get("x").toString())));
                a_array[i][1] = 2 * ((Double.valueOf(apArray.get(i).get("y").toString())) -(Double.valueOf(apArray.get(2).get("y").toString())));
         }

        //矩阵b初始化
        for (int i = 0; i< 2; i++)
        {
            b_array[i][0] = Math.pow(Double.valueOf(apArray.get(i).get("x").toString()), 2)
                    - Math.pow(Double.valueOf(apArray.get(2).get("x").toString()), 2)
                    + Math.pow(Double.valueOf(apArray.get(i).get("y").toString()), 2)
                    - Math.pow(Double.valueOf(apArray.get(2).get("y").toString()), 2)
                    + Math.pow(distanceArray[2], 2)
                    - Math.pow(distanceArray[i], 2);
        }

        Matrix A = new Matrix(a_array);
        Matrix B = new Matrix(b_array);

        Matrix a1 = A.transpose();
        Matrix a2 = a1.times(A);
        Matrix a3 = a2.inverse().times(a1);
        Matrix resultX = a3.times(B);
        double[][] res = resultX.getArray();

        /*对应的权值*/
        double weight = 0;
        for (int i = 0; i < 3; i++)
        {
            weight += (1.0 / distanceArray[i]);
        }

        weight=Double.valueOf(String.format("%.1f", weight));
        totalWeight += weight;
        point.put("x",Double.valueOf(String.format("%.1f", res[0][0]*weight)));
        point.put("y",Double.valueOf(String.format("%.1f", res[1][0]*weight)));
        return point;
    }


    //计算加权后的坐标
    public Map<String, Integer>cpoint(Double totalX,Double totalY,String atAddress){
        Integer MinX=null;
        Integer MinY=null;
        Map<String, Integer> point=new HashMap<>();
        Double x=totalX/totalWeight;
        Double y=totalY/totalWeight;
        int x1= (int)Math.round(x);
        int y1=(int)Math.round(y);
        Integer x2=Integer.valueOf(x1);
        Integer y2=Integer.valueOf(y1);

        point.put("macx",x2);
        point.put("macy",y2);
        totalWeight = 0;
        return  point;
    }

    //根据区域x、y的范围值，判断在哪个区域
    public String judgeClass(Integer x,Integer y,String indoorname){
        String atAdress = null;
        Integer MinX=null;
        Integer MaxX=null;
        Integer MinY=null;
        Integer MaxY=null;
        List<Class> classes = classService.list3(indoorname);
        for(Class c:classes){
            List<String> list1 = Arrays.asList(StringUtils.split(c.getX1(), ","));
            List<String> list2 = Arrays.asList(StringUtils.split(c.getX2(), ","));
            List<String> list3 = Arrays.asList(StringUtils.split(c.getY1(), ","));
            List<String> listX1 = new ArrayList<>(list1);
            List<String> listX2 = new ArrayList<>(list2);
            List<String> listY1 = new ArrayList<>(list3);
            MinX=Integer.valueOf(listX1.get(0));
            MinY=Integer.valueOf(listX1.get(1));
            MaxX=Integer.valueOf(listX2.get(0));
            MaxY=Integer.valueOf(listY1.get(1));
            if(MinX<x && x<MaxX && MinY<y && y<MaxY){
                atAdress=c.getAdress();
            }
        }
        return atAdress;
    }

    //根据区域名，判断是否为禁止区域
    public Integer Stopjudge(String addressname,String indoorname){
        return classService.listbyaddress2(addressname,indoorname).get(0).getStopjudge();
    }

}
