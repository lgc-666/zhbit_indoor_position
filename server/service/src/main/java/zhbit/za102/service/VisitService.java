package zhbit.za102.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zhbit.za102.bean.*;
import zhbit.za102.dao.VisitMapper;

import java.util.List;

@Service
@CacheConfig(cacheNames = "Visit")
public class VisitService {
    @Autowired
    VisitMapper visitMapper;

    @CacheEvict(value="Visit",allEntries = true)
    public void add(Visit u) {
        visitMapper.insert(u);
    }

    @CacheEvict(value="Visit",allEntries = true)
    public void delete(Integer id) {
        visitMapper.deleteByPrimaryKey(id);
    }

    @CacheEvict(value="Visit",allEntries = true)
    public void update(Visit u) {
        visitMapper.updateByPrimaryKeySelective(u);
    }

    @Cacheable(value="Visit",key = "'get'+'-'+#id")
    public Visit get(Integer id) {
        return visitMapper.selectByPrimaryKey(id);
    }

    public List<Visit> list() {
        VisitExample example = new VisitExample();
        example.setOrderByClause("visitid desc");
        return visitMapper.selectByExample(example);
    }

    public List<Visit> list2(String staffdata) {
        VisitExample example = new VisitExample();
        example.createCriteria().andAddressEqualTo(staffdata);
        example.setOrderByClause("visitid desc");
        return visitMapper.selectByExample(example);
    }

    @Cacheable(value="Visit",key = "'list'+'-'+#start+'-'+#size")
    public Msg list(int start, int size) {
        PageHelper.startPage(start, size, "visitid desc");
        List<Visit> us = list();
        PageInfo<Visit> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public Msg listSearch(String staffdata,int start, int size) {
        PageHelper.startPage(start, size, "visitid desc");
        List<Visit> us = list2(staffdata);
        PageInfo<Visit> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public List<Visit> findvisitByMac(String mac,String address){
        VisitExample example = new VisitExample();
        example.createCriteria().andMacEqualTo(mac).andAddressEqualTo(address);
        return visitMapper.selectByExample(example);
    }

    public SumVisit getSumVisit(String dateTime, String address,String indoorname) {
        return visitMapper.getSumVisit(dateTime, address,indoorname);
    }

    public SumStoptime getSumStoptime(String dateTime, String address,String indoorname) {
        return visitMapper.getSumStoptime(dateTime, address, indoorname);
    }

    public Integer getsortNow(String address,String indoorname) {
        return visitMapper.getsortNow(address,indoorname).getNowcount();
    }

    public Msg list3(List<Integer> us, int start, int size) {
        PageHelper.startPage(start, size);
        VisitExample example = new VisitExample();
        example.createCriteria().andVisitidIn(us);
        List<Visit> a= visitMapper.selectByExample(example);
        PageInfo<Visit> page = new PageInfo<>(a);
        return new Msg(page);
    }

    public List<Visit> list4(String indoorname) {
        VisitExample example = new VisitExample();
        example.createCriteria().andIndoornameEqualTo(indoorname);
        //example.setOrderByClause("mid desc");
        return visitMapper.selectByExample(example);
    }

    public List<Visit> list5(String indoorname,String staffdata) {
        VisitExample example = new VisitExample();
        example.createCriteria().andAddressEqualTo(staffdata).andIndoornameEqualTo(indoorname);
        //example.setOrderByClause("mid desc");
        return visitMapper.selectByExample(example);
    }
}
