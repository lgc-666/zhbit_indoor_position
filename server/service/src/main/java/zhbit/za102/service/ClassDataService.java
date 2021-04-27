package zhbit.za102.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zhbit.za102.bean.*;
import zhbit.za102.dao.ClassDataMapper;

import java.util.List;

@Service
@CacheConfig(cacheNames = "ClassData")
public class ClassDataService {
    @Autowired
    ClassDataMapper classDataMapper;

    @CacheEvict(value="ClassData",allEntries = true)
    public void add(ClassData u) {
        classDataMapper.insert(u);
    }

    @CacheEvict(value="ClassData",allEntries = true)
    public void delete(Integer id) {
        classDataMapper.deleteByPrimaryKey(id);
    }

    @CacheEvict(value="ClassData",allEntries = true)
    public void update(ClassData u) {
        classDataMapper.updateByPrimaryKeySelective(u);
    }

    @Cacheable(value="ClassData",key = "'get'+'-'+#id")
    public ClassData get(Integer id) {
        return classDataMapper.selectByPrimaryKey(id);
    }

    public List<ClassData> list() {
        ClassDataExample example = new ClassDataExample();
        example.setOrderByClause("id desc");
        return classDataMapper.selectByExample(example);
    }

    //获取去重复记录
    public Integer listdis() {
        return classDataMapper.listdis();
    }

    @Cacheable(key = "'list'+'-'+#start+'-'+#size")
    public Msg list(int start, int size) {
        PageHelper.startPage(start, size, "id desc");
        List<ClassData> us = list();
        PageInfo<ClassData> page = new PageInfo<>(us);
        return new Msg(page);
    }


    public Integer selectWithin1hour(){
        return classDataMapper.selectWithin1hour();
    }
    public Integer selectWithin1hourByClass(){
        return classDataMapper.selectWithin1hourByClass();
    }
    public void updateWithin1hour(Integer hours,Integer num){ classDataMapper.updateWithin1hour(hours,num); }
    public void insertClassData(String address,Integer hours,String indoorname){ classDataMapper.insertClassData(address,hours,indoorname); }

    public List<SumData> getSum(String dateTime,String indoorname) {//获取当天室内总数据
        return classDataMapper.getSum(dateTime,indoorname);
    }

    public List<ClassData> getMainData(String address,String dateTime,String indoorname) {//获取主要数据
        return classDataMapper.getMainData(address,dateTime,indoorname);
    }

    public List<CustomerPerHour> getCustomerPerHour(String address, String dateTime, String indoorname) {//获取主要数据
        return classDataMapper.getCustomerPerHour(address,dateTime,indoorname);
    }
    public List<InCustomerPerHour> getInCustomerPerHour(String address, String dateTime,String indoorname) {//获取主要数据
        return classDataMapper.getInCustomerPerHour(address,dateTime,indoorname);
    }
}
