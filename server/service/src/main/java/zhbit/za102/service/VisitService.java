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

    @Cacheable(value="Visit",key = "'list'+'-'+#start+'-'+#size")
    public Msg list(int start, int size) {
        PageHelper.startPage(start, size, "visitid desc");
        List<Visit> us = list();
        PageInfo<Visit> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public List<Visit> findvisitByMac(String mac,String address){
        VisitExample example = new VisitExample();
        example.createCriteria().andMacEqualTo(mac).andAddressEqualTo(address);
        return visitMapper.selectByExample(example);
    }

    public SumVisit getSumVisit(String dateTime, String address) {
        return visitMapper.getSumVisit(dateTime, address);
    }

    public SumStoptime getSumStoptime(String dateTime, String address) {
        return visitMapper.getSumStoptime(dateTime, address);
    }

    public Integer getsortNow(String address) {
        return visitMapper.getsortNow(address).getNowcount();
    }
}
