package zhbit.za102.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import zhbit.za102.Utils.RedisUtils;
import zhbit.za102.bean.*;
import zhbit.za102.dao.DeviceMapper;
import zhbit.za102.dao.LogrecordMapper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
@CacheConfig(cacheNames = "Device")
public class DeviceService {
    @Resource
    RedisUtils redisUtil;
    @Autowired
    DeviceMapper deviceMapper;
    @Autowired
    LogrecordService logrecordService;
    @Autowired
    DeviceService deviceService;

    public DeviceService() throws SocketException {
    }

    @CacheEvict(value="Device",allEntries = true)
    public void add(Device u) {
        deviceMapper.insert(u);
    }

    @CacheEvict(value="Device",allEntries = true)
    public void delete(Integer id) { deviceMapper.deleteByPrimaryKey(id); }

    @CacheEvict(value="Device",allEntries = true)
    public void update(Device u) { deviceMapper.updateByPrimaryKeySelective(u); }

    @Cacheable(value="Device",key = "'get'+'-'+#id")
    public Device get(Integer id) {
        return deviceMapper.selectByPrimaryKey(id);
    }

    public List<Device> list() {
        DeviceExample example = new DeviceExample();
        example.setOrderByClause("deviceid desc");
        return deviceMapper.selectByExample(example);
    }

    public List<Device> list2(String staffdata) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIdEqualTo(staffdata);
        example.setOrderByClause("deviceid desc");
        return deviceMapper.selectByExample(example);
    }

    public List<Device> list5(String indoorname,String staffdata) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIdEqualTo(staffdata).andIndoornameEqualTo(indoorname);
        //example.setOrderByClause("deviceid desc");
        return deviceMapper.selectByExample(example);
    }

    public List<Device> list4(String indoorname) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIndoornameEqualTo(indoorname);
        //example.setOrderByClause("deviceid desc");
        return deviceMapper.selectByExample(example);
    }

    @Cacheable(value="Device",key = "'list'+'-'+#start+'-'+#size")
    public Msg list(int start, int size) {
        PageHelper.startPage(start, size, "deviceid desc");
        List<Device> us = list();
        PageInfo<Device> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public Msg listSearch(String staffdata,int start, int size) {
        PageHelper.startPage(start, size, "deviceid desc");
        List<Device> us = list2(staffdata);
        PageInfo<Device> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public Msg list3(List<String> us, int start, int size) {
        PageHelper.startPage(start, size);
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIdIn(us);
        List<Device> a= deviceMapper.selectByExample(example);
        PageInfo<Device> page = new PageInfo<>(a);
        return new Msg(page);
    }

    public List<Device> listbyId(String id,String indoorname) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIdEqualTo(id).andIndoornameEqualTo(indoorname);
        return deviceMapper.selectByExample(example);
    }

    public List<Device> listbyId2(String id) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIdEqualTo(id);
        return deviceMapper.selectByExample(example);
    }

    @CacheEvict(value="Device",allEntries = true)
    public void insertdevice(String deviceid,String devicetype,String devicevalue,String lasttime,String ip,Integer port,String gentime,String indoorname){
        deviceMapper.insertdevice(deviceid,devicetype,devicevalue,lasttime,ip,port,gentime,indoorname);
    }

    public List<Device> listbyAdress(String address,String indoorname) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andLocationEqualTo(address).andDevicetypeEqualTo("5").andIndoornameEqualTo(indoorname);  //类型5为报警器
        return deviceMapper.selectByExample(example);
    }

    public List<Device> listbyAdressLight(String address,String indoorname) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andLocationEqualTo(address).andDevicetypeEqualTo("6").andIndoornameEqualTo(indoorname);  //类型6为灯
        return deviceMapper.selectByExample(example);
    }


    DatagramSocket ds = new DatagramSocket(3020);
    String control_value="";
    byte [] bbuf = new byte [1024];
    DatagramPacket dp = new DatagramPacket(bbuf,bbuf.length);
    public void monitor() throws Exception
    {
            while (true) {
                synchronized (this) {
                    try{
                        ds.receive(dp);
                        String mess1 = new String(dp.getData());
                        System.out.println("mess：" + mess1);
                        String ip = dp.getAddress().getHostAddress();  //ip地址
                        int port = dp.getPort();               //端口号

                        String deviceid = mess1.split(",")[0];
                        String devicetype = mess1.split(",")[1];
                        String devicevalue = mess1.split(",")[2].trim();
                        //String indoorname = mess1.split(",")[3].trim();  //新加的地图数据
                        if(listbyId2(deviceid).size()!=0&&listbyId2(deviceid)!=null){
                            String indoorname = listbyId2(deviceid).get(0).getIndoorname();
                            List<List<String>> a = (List<List<String>>) (List) redisUtil.lGet("MachineList", 0, -1);
                            if (a.size() != 0 && a != null) {
                                List<String> c = a.get(0);
                                if (c.size() != 0 && c != null) {
                                    for (int i = 0; i < c.size(); i++) {
                                        if(logrecordService.listbyId(c.get(i))!=null&&logrecordService.listbyId(c.get(i)).size()!=0){
                                            String kvalue = logrecordService.listbyId(c.get(i)).get(0).getChangevalue();

                                            if (c.get(i).equals(deviceid))   //按钮传来的id与设备id相同时才做设备控制操作
                                            {
                                                System.out.println("同一个设备id");
                                                //判断是否有控制指令且控制指令不同于当前状态值
                                                if (kvalue.equals(devicevalue))
                                                {
                                                    check(deviceid, devicetype, devicevalue, ip, port, indoorname);
                                                    c.remove(c.get(i));
                                                    redisUtil.del("MachineList");
                                                    if (c.size() != 0) {
                                                        redisUtil.lSet("MachineList", c);
                                                    } else {
                                                        break;
                                                    }
                                                } else {
                                                    if (kvalue.equals("0")) {
                                                        control_value = c.get(i) + ",SWITCH0";
                                                    } else {
                                                        control_value = c.get(i) + ",SWITCH1";
                                                    }
                                                    try {
                                                        byte[] bs = control_value.getBytes();
                                                        DatagramPacket dp2 = new DatagramPacket(bs, bs.length, InetAddress.getByName(ip), port);
                                                        try {
                                                            ds.send(dp2);
                                                            check(deviceid, devicetype, devicevalue, ip, port, indoorname);
                                                        } catch (IOException e) {
                                                            System.out.println("发送失败： ");
                                                            e.printStackTrace();
                                                        }
                                                    } catch (Exception e) {
                                                        System.out.println("Error:" + e);
                                                    }
                                                }
                                            }
                                        }
                                        else{
                                            System.out.println("没有要处理的请求！！！");
                                        }
                                    }
                                } else {
                                }
                            } else {
                            }
                        }
                        }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    //改数据库中设备状态
    public void check(String deviceid,String devicetype,String devicevalue,String ip,Integer port,String indoorname){
        Date t=new Date();
        System.out.println(t);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dt = df.format(t);
        if(listbyId(deviceid,indoorname).size()!=0)
        {
            String gentime = df.format(new Date());
            long now1 = System.currentTimeMillis();
            now1=now1/1000;
            String lasttime=String.valueOf(now1);
            Device d1 =new Device();
            d1.setId(deviceid);
            d1.setDevicetype(devicetype);
            d1.setDevicevalue(devicevalue);
            d1.setLasttime(lasttime);
            d1.setIp(ip);
            d1.setPort(port);
            d1.setGentime(gentime);
            d1.setDeviceid(deviceService.listbyId(deviceid,indoorname).get(0).getDeviceid());
            d1.setIndoorname(indoorname);
            deviceService.update(d1);
        }
        else{
            String gentime = df.format(new Date());
            long now1 = System.currentTimeMillis();
            now1=now1/1000;
            String lasttime=String.valueOf(now1);
            deviceService.insertdevice(deviceid,devicetype,devicevalue,lasttime,ip,port,gentime,indoorname);    //插入设备信息
        }
    }

    @Caching(evict={
            @CacheEvict(value="ClassData",allEntries = true),
            @CacheEvict(value="Class",allEntries = true),
            @CacheEvict(value="Device",allEntries = true),
            @CacheEvict(value="Logrecord",allEntries = true),
            @CacheEvict(value="Machine",allEntries = true),
            @CacheEvict(value="MapMamage",allEntries = true),
            @CacheEvict(value="Permission", allEntries=true),
            @CacheEvict(value="RegisterApproval",allEntries = true),
            @CacheEvict(value="Role", allEntries=true),
            @CacheEvict(value="Visit",allEntries = true),
            @CacheEvict(value="StopVisit",allEntries = true),
            @CacheEvict(value="User", allEntries=true),
    })
    public void deleteAllCach(){
    }
}
