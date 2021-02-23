package zhbit.za102.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zhbit.za102.bean.*;
import zhbit.za102.dao.DeviceMapper;
import zhbit.za102.dao.LogrecordMapper;

import java.util.List;

@Service
@CacheConfig(cacheNames = "Device")
public class DeviceService {
    @Autowired
    DeviceMapper deviceMapper;

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

    @Cacheable(value="Device",key = "'list'+'-'+#start+'-'+#size")
    public Msg list(int start, int size) {
        PageHelper.startPage(start, size, "deviceid desc");
        List<Device> us = list();
        PageInfo<Device> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public List<Device> listbyId(String id) {
        DeviceExample example = new DeviceExample();
        example.createCriteria().andIdEqualTo(id);
        return deviceMapper.selectByExample(example);
    }

    @CacheEvict(value="Device",allEntries = true)
    public void insertdevice(String deviceid,String devicetype,String devicevalue,String lasttime,String ip,Integer port,String gentime){
        deviceMapper.insertdevice(deviceid,devicetype,devicevalue,lasttime,ip,port,gentime);
    }
}
