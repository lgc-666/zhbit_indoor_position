package zhbit.za102.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zhbit.za102.bean.Msg;
import zhbit.za102.bean.StopVisit;
import zhbit.za102.bean.StopVisitExample;
import zhbit.za102.dao.StopVisitMapper;

import java.util.List;

@Service
@CacheConfig(cacheNames = "StopVisit")
public class StopVisitService {
    @Autowired
    StopVisitMapper stopVisitMapper;

    @CacheEvict(value="StopVisit",allEntries = true)
    public void add(StopVisit u) {
        stopVisitMapper.insert(u);
    }

    @CacheEvict(value="StopVisit",allEntries = true)
    public void delete(Integer id) {
        stopVisitMapper.deleteByPrimaryKey(id);
    }

    @CacheEvict(value="StopVisit",allEntries = true)
    public void update(StopVisit u) {
        stopVisitMapper.updateByPrimaryKeySelective(u);
    }

    @Cacheable(value="StopVisit",key = "'get'+'-'+#id")
    public StopVisit get(Integer id) {
        return stopVisitMapper.selectByPrimaryKey(id);
    }

    @Cacheable(value="StopVisit",key = "'list'")
    public List<StopVisit> list() {
        StopVisitExample example = new StopVisitExample();
        example.setOrderByClause("stop_visit_id desc");
        return stopVisitMapper.selectByExample(example);
    }

    public List<StopVisit> list2(String staffdata) {
        StopVisitExample example = new StopVisitExample();
        example.createCriteria().andAddressEqualTo(staffdata);
        example.setOrderByClause("stop_visit_id desc");
        return stopVisitMapper.selectByExample(example);
    }

    @Cacheable(value="StopVisit",key = "'list'+'-'+#start+'-'+#size")
    public Msg list(int start, int size) {
        PageHelper.startPage(start, size, "stop_visit_id desc");
        List<StopVisit> us = list();
        PageInfo<StopVisit> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public Msg listSearch(String staffdata,int start, int size) {
        PageHelper.startPage(start, size, "stop_visit_id desc");
        List<StopVisit> us = list2(staffdata);
        PageInfo<StopVisit> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public List<StopVisit> findstopVisitByMac(String mac){
        StopVisitExample example = new StopVisitExample();
        example.createCriteria().andMacEqualTo(mac);
        return stopVisitMapper.selectByExample(example);
    }

    public Msg list3(List<Integer> us, int start, int size) {
        PageHelper.startPage(start, size);
        StopVisitExample example = new StopVisitExample();
        example.createCriteria().andStopVisitIdIn(us);
        List<StopVisit> a= stopVisitMapper.selectByExample(example);
        PageInfo<StopVisit> page = new PageInfo<>(a);
        return new Msg(page);
    }

    public List<StopVisit> list4(String indoorname) {
        StopVisitExample example = new StopVisitExample();
        example.createCriteria().andIndoornameEqualTo(indoorname);
        //example.setOrderByClause("mid desc");
        return stopVisitMapper.selectByExample(example);
    }

    public List<StopVisit> list5(String indoorname,String staffdata) {
        StopVisitExample example = new StopVisitExample();
        example.createCriteria().andAddressEqualTo(staffdata).andIndoornameEqualTo(indoorname);
        //example.setOrderByClause("mid desc");
        return stopVisitMapper.selectByExample(example);
    }
}
