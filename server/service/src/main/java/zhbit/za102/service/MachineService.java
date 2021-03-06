package zhbit.za102.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import zhbit.za102.bean.Machine;
import zhbit.za102.bean.MachineExample;
import zhbit.za102.bean.Msg;
import zhbit.za102.dao.MachineMapper;

import java.util.List;

@Service
@CacheConfig(cacheNames = "Machine")
public class MachineService {
    @Autowired
    MachineMapper machineMapper;

    @CacheEvict(value="Machine",allEntries = true)
    public void add(Machine u) {
        machineMapper.insert(u);
    }

    @CacheEvict(value="Machine",allEntries = true)
    public void delete(Integer id) {
        machineMapper.deleteByPrimaryKey(id);
    }

    @CacheEvict(value="Machine",allEntries = true)
    public void update(Machine u) {
        machineMapper.updateByPrimaryKeySelective(u);
    }

    @Cacheable(value="Machine",key = "'get'+'-'+#id")
    public Machine get(Integer id) {
        return machineMapper.selectByPrimaryKey(id);
    }

    public List<Machine> list() {
        MachineExample example = new MachineExample();
        example.setOrderByClause("mid desc");
        return machineMapper.selectByExample(example);
    }

    public List<Machine> list2(String staffdata) {
        MachineExample example = new MachineExample();
        example.createCriteria().andMachineidEqualTo(staffdata);
        example.setOrderByClause("mid desc");
        return machineMapper.selectByExample(example);
    }

    public List<Machine> listAll() {
        MachineExample example = new MachineExample();
        example.setOrderByClause("mid");
        return machineMapper.selectByExample(example);
    }

    @Cacheable(value="Machine",key = "'list'+'-'+#start+'-'+#size")
    public Msg list(int start, int size) {
        PageHelper.startPage(start, size, "mid desc");
        List<Machine> us = list();
        PageInfo<Machine> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public Msg listSearch(String staffdata,int start, int size) {
        PageHelper.startPage(start, size, "mid desc");
        List<Machine> us = list2(staffdata);
        PageInfo<Machine> page = new PageInfo<>(us);
        return new Msg(page);
    }

    public List<Machine> listbyId2(String id) {
        MachineExample example = new MachineExample();
        example.createCriteria().andMachineidEqualTo(id);
        return machineMapper.selectByExample(example);
    }

    public List<Machine> list4(String indoorname) {
        MachineExample example = new MachineExample();
        example.createCriteria().andIndoornameEqualTo(indoorname);
        return machineMapper.selectByExample(example);
    }
    public List<Machine> list5(String indoorname,String staffdata) {
        MachineExample example = new MachineExample();
        example.createCriteria().andMachineidEqualTo(staffdata).andIndoornameEqualTo(indoorname);
        return machineMapper.selectByExample(example);
    }
    public Msg list3(List<String> us, int start, int size) {
        PageHelper.startPage(start, size);
        MachineExample example = new MachineExample();
        example.createCriteria().andMachineidIn(us);
        List<Machine> a= machineMapper.selectByExample(example);
        PageInfo<Machine> page = new PageInfo<>(a);
        return new Msg(page);
    }
}
