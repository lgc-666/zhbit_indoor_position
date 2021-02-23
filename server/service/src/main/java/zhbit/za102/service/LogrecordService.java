package zhbit.za102.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zhbit.za102.bean.*;
import zhbit.za102.dao.LogrecordMapper;

import java.util.List;

@Service
@CacheConfig(cacheNames = "Logrecord")
public class LogrecordService {
    @Autowired
    LogrecordMapper logrecordMapper;

    @CacheEvict(value="Logrecord",allEntries = true)
    public void add(Logrecord u) {
        logrecordMapper.insert(u);
    }

    @CacheEvict(value="Logrecord",allEntries = true)
    public void delete(Integer id) { logrecordMapper.deleteByPrimaryKey(id); }

    @CacheEvict(value="Logrecord",allEntries = true)
    public void update(Logrecord u) { logrecordMapper.updateByPrimaryKeySelective(u); }

    @Cacheable(value="Logrecord",key = "'get'+'-'+#id")
    public Logrecord get(Integer id) {
        return logrecordMapper.selectByPrimaryKey(id);
    }

    public List<Logrecord> list() {
        LogrecordExample example = new LogrecordExample();
        example.setOrderByClause("logid desc");
        return logrecordMapper.selectByExample(example);
    }

    @Cacheable(value="Logrecord",key = "'list'+'-'+#start+'-'+#size")
    public Msg list(int start, int size) {
        PageHelper.startPage(start, size, "logid desc");
        List<Logrecord> us = list();
        PageInfo<Logrecord> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public List<String> findAllLogrecordId(){
        return logrecordMapper.findAllLogrecord();
    }

    public List<Logrecord> listbyId(String id) {
        LogrecordExample example = new LogrecordExample();
        example.createCriteria().andIdEqualTo(id);
        example.setOrderByClause("logid desc");
        return logrecordMapper.selectByExample(example);
    }

    @CacheEvict(value="Logrecord",allEntries = true)
    public void addchange(String id,String changevalue,String gentime){
        logrecordMapper.addchange(id,changevalue,gentime);
    }
}
