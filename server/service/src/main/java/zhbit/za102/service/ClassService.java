package zhbit.za102.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zhbit.za102.bean.Class;
import zhbit.za102.bean.ClassExample;
import zhbit.za102.bean.Msg;
import zhbit.za102.dao.ClassMapper;

import java.util.List;

@Service
@CacheConfig(cacheNames = "Class")
public class ClassService {
    @Autowired
    ClassMapper classMapper;

    @CacheEvict(value="Class",allEntries = true)
    public void add(Class u) {
        classMapper.insert(u);
    }

    @CacheEvict(value="Class",allEntries = true)
    public void delete(Integer id) { classMapper.deleteByPrimaryKey(id); }

    @CacheEvict(value="Class",allEntries = true)
    public void update(Class u) { classMapper.updateByPrimaryKeySelective(u); }

    @Cacheable(value="Class",key = "'get'+'-'+#id")
    public Class get(Integer id) {
        return classMapper.selectByPrimaryKey(id);
    }


    public List<Class> list() {
        ClassExample example = new ClassExample();
        example.setOrderByClause("classid desc");
        return classMapper.selectByExample(example);
    }

    public List<Class> listPublic() {
        ClassExample example = new ClassExample();
        example.createCriteria().andStopjudgeEqualTo(0);
        example.setOrderByClause("classid desc");
        return classMapper.selectByExample(example);
    }

    public List<Class> listStop() {
        ClassExample example = new ClassExample();
        example.createCriteria().andStopjudgeEqualTo(1);
        example.setOrderByClause("classid desc");
        return classMapper.selectByExample(example);
    }

    public List<Class> list2(String staffdata) {
        ClassExample example = new ClassExample();
        example.createCriteria().andAdressEqualTo(staffdata);
        example.setOrderByClause("classid desc");
        return classMapper.selectByExample(example);
    }

    public List<Class> list3(String indoorname) {
        ClassExample example = new ClassExample();
        example.createCriteria().andIndoornameEqualTo(indoorname);
        //example.setOrderByClause("classid desc");
        return classMapper.selectByExample(example);
    }

    @Cacheable(value="Class",key = "'list'+'-'+#start+'-'+#size")
    public Msg list(int start, int size) {
        PageHelper.startPage(start, size, "classid desc");
        List<Class> us = list();
        PageInfo<Class> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public Msg listSearch(String staffdata,int start, int size) {
        PageHelper.startPage(start, size, "classid desc");
        List<Class> us = list2(staffdata);
        PageInfo<Class> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public List<String> findAllClass(){
        return classMapper.findAllClass();
    }

    public List<Class> findAllClassBean(){
        return classMapper.findAllClassBean();
    }


    public List<Class> listbyaddress(String addressname) {
        ClassExample example = new ClassExample();
        example.createCriteria().andAdressEqualTo(addressname);
        return classMapper.selectByExample(example);
    }

    public List<Class> listbyaddress2(String addressname,String indoorname) {
        ClassExample example = new ClassExample();
        example.createCriteria().andAdressEqualTo(addressname).andIndoornameEqualTo(indoorname);
        return classMapper.selectByExample(example);
    }

    public List<Class> list5(String indoorname,String staffdata) {
        ClassExample example = new ClassExample();
        example.createCriteria().andAdressEqualTo(staffdata).andIndoornameEqualTo(indoorname);
        //example.setOrderByClause("mid desc");
        return classMapper.selectByExample(example);
    }

    public List<Class> list4(String indoorname) {
        ClassExample example = new ClassExample();
        example.createCriteria().andIndoornameEqualTo(indoorname);
        //example.setOrderByClause("mid desc");
        return classMapper.selectByExample(example);
    }

    public Msg list3(List<Integer> us, int start, int size) {
        PageHelper.startPage(start, size);//只对该语句以后的第一个查询语句得到的数据进行分页
        ClassExample example = new ClassExample();
        example.createCriteria().andClassidIn(us);
        List<Class> a= classMapper.selectByExample(example);
        PageInfo<Class> page = new PageInfo<>(a);
        return new Msg(page);
    }
}
