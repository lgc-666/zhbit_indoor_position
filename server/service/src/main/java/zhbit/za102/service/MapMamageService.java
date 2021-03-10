package zhbit.za102.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zhbit.za102.bean.Msg;
import zhbit.za102.bean.map_mamage;
import zhbit.za102.bean.map_mamageExample;
import zhbit.za102.dao.map_mamageMapper;

import java.util.List;

@Service
@CacheConfig(cacheNames = "MapMamage")
public class MapMamageService {
    @Autowired
    map_mamageMapper mapMamageMapper;

    @CacheEvict(value="MapMamage",allEntries = true)
    public void add(map_mamage u) {
        mapMamageMapper.insert(u);
    }

    @CacheEvict(value="MapMamage",allEntries = true)
    public void delete(Integer id) {
        mapMamageMapper.deleteByPrimaryKey(id);
    }

    @CacheEvict(value="MapMamage",allEntries = true)
    public void update(map_mamage u) {
        mapMamageMapper.updateByPrimaryKeySelective(u);
    }

    @Cacheable(value="MapMamage",key = "'get'+'-'+#id")
    public map_mamage get(Integer id) {
        return mapMamageMapper.selectByPrimaryKey(id);
    }

    public List<map_mamage> list() {
        map_mamageExample example = new map_mamageExample();
        example.setOrderByClause("id desc");
        return mapMamageMapper.selectByExample(example);
    }

    @Cacheable(key = "'list'+'-'+#start+'-'+#size")
    public Msg list(int start, int size) {
        PageHelper.startPage(start, size, "id desc");
        List<map_mamage> us = list();
        PageInfo<map_mamage> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public List<map_mamage> list2(String staffdata) {
        map_mamageExample example = new map_mamageExample();
        example.createCriteria().andChargeEqualTo(staffdata);
        example.setOrderByClause("id desc");
        return mapMamageMapper.selectByExample(example);
    }
    public Msg listSearch(String staffdata,int start, int size) {
        PageHelper.startPage(start, size, "id desc");
        List<map_mamage> us = list2(staffdata);
        PageInfo<map_mamage> page = new PageInfo<>(us);
        return new Msg(page);
    }
}
