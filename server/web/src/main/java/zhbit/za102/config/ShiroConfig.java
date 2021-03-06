package zhbit.za102.config;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zhbit.za102.Filter.ShiroLogoutFilter;
import zhbit.za102.Filter.URLPathMatchingFilter;
import zhbit.za102.realm.DatabaseRealm;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    @Bean
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager){
        System.out.println("ShiroConfiguration.shirFilter()");
        ShiroFilterFactoryBean shiroFilterFactoryBean  = new ShiroFilterFactoryBean();

        // 必须设置 SecurityManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");
        //拦截器.
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String,String>();
        //自定义拦截器
        Map<String, Filter> customisedFilter = new HashMap<>();
        customisedFilter.put("url", getURLPathMatchingFilter());
        //配置自定义登出, 覆盖掉 logout 之前默认的LogoutFilter
        customisedFilter.put("logout", shiroLogoutFilter());


        /**
         *配置映射关系
        */
        filterChainDefinitionMap.put("/loginUser","anon");
        filterChainDefinitionMap.put("/login","anon");
        filterChainDefinitionMap.put("/register","anon");
        filterChainDefinitionMap.put("/doLogout", "logout");

        //开放其他接口(登录后可以访问)
        filterChainDefinitionMap.put("/menu","authc");

        filterChainDefinitionMap.put("/listUser","perms[listUser]");
        filterChainDefinitionMap.put("/updatePassword", "perms[updatePassword]");
        filterChainDefinitionMap.put("/updateUser", "perms[updateUser]");
        filterChainDefinitionMap.put("/listUserSearch", "perms[listUserSearch]");

        filterChainDefinitionMap.put("/listRole", "perms[listRole]");
        filterChainDefinitionMap.put("/editRole", "perms[editRole]");
        filterChainDefinitionMap.put("/updateRolePermission", "perms[updateRolePermission]");

        filterChainDefinitionMap.put("/listregisterApproval", "perms[listregisterApproval]");
        filterChainDefinitionMap.put("/agreeregisterApproval", "perms[agreeregisterApproval]");
        filterChainDefinitionMap.put("/deleteregisterApproval", "perms[deleteregisterApproval]");

        filterChainDefinitionMap.put("/listmachine", "perms[listmachine]");
        filterChainDefinitionMap.put("/deletemachine", "perms[deletemachine]");
        filterChainDefinitionMap.put("/updatemachine", "perms[updatemachine]");
        filterChainDefinitionMap.put("/addmachine", "perms[addmachine]");
        filterChainDefinitionMap.put("/listmachineSearch", "perms[listmachineSearch]");

        filterChainDefinitionMap.put("/listClassNoPagePublic", "perms[listClassNoPagePublic]");
        filterChainDefinitionMap.put("/listClassNoPageStop", "perms[listClassNoPageStop]");
        filterChainDefinitionMap.put("/listClassNoPage", "perms[listClassNoPage]");
        filterChainDefinitionMap.put("/listClassNoPage2", "perms[listClassNoPage2]");
        filterChainDefinitionMap.put("/listClass", "perms[listClass]");
        filterChainDefinitionMap.put("/deleteClass", "perms[deleteClass]");
        filterChainDefinitionMap.put("/updateClass", "perms[updateClass]");
        filterChainDefinitionMap.put("/addClass", "perms[addClass]");
        filterChainDefinitionMap.put("/listClassSearch", "perms[listClassSearch]");

        filterChainDefinitionMap.put("/getDBlocation", "perms[getDBlocation]");
        filterChainDefinitionMap.put("/getDBlocationNotRepeat", "perms[getDBlocationNotRepeat]");
        filterChainDefinitionMap.put("/listByMac", "perms[listByMac]");

        filterChainDefinitionMap.put("/listVisit", "perms[listVisit]");
        filterChainDefinitionMap.put("/deleteVisit", "perms[deleteVisit]");
        filterChainDefinitionMap.put("/updateVisit", "perms[updateVisit]");
        filterChainDefinitionMap.put("/addVisit", "perms[addVisit]");
        filterChainDefinitionMap.put("/listVisitSearch", "perms[listVisitSearch]");

        filterChainDefinitionMap.put("/listStopVisit", "perms[listStopVisit]");
        filterChainDefinitionMap.put("/deleteStopVisit", "perms[deleteStopVisit]");
        filterChainDefinitionMap.put("/updateStopVisit", "perms[updateStopVisit]");
        filterChainDefinitionMap.put("/addStopVisit", "perms[addStopVisit]");
        filterChainDefinitionMap.put("/listStopVisitSearch", "perms[listStopVisitSearch]");

        filterChainDefinitionMap.put("/listDevice", "perms[listDevice]");
        filterChainDefinitionMap.put("/deleteDevice", "perms[deleteDevice]");
        filterChainDefinitionMap.put("/updateDevice", "perms[updateDevice]");
        filterChainDefinitionMap.put("/addDevice", "perms[addDevice]");
        filterChainDefinitionMap.put("/listDeviceSearch", "perms[listDeviceSearch]");

        filterChainDefinitionMap.put("/listLogrecord", "perms[listLogrecord]");
        filterChainDefinitionMap.put("/deleteLogrecord", "perms[deleteLogrecord]");
        filterChainDefinitionMap.put("/updateLogrecord", "perms[updateLogrecord]");
        filterChainDefinitionMap.put("/addLogrecord", "perms[addLogrecord]");
        filterChainDefinitionMap.put("/listLogrecordSearch", "perms[listLogrecordSearch]");

        filterChainDefinitionMap.put("/getSum", "perms[getSum]");
        filterChainDefinitionMap.put("/getMainData", "perms[getMainData]");
        filterChainDefinitionMap.put("/getCustomerPerHour", "perms[getCustomerPerHour]");
        filterChainDefinitionMap.put("/getInCustomerPerHour", "perms[getInCustomerPerHour]");

        filterChainDefinitionMap.put("/sortVisit", "perms[sortVisit]");
        filterChainDefinitionMap.put("/sortStoptime", "perms[sortStoptime]");
        filterChainDefinitionMap.put("/sortNow", "perms[sortNow]");

        filterChainDefinitionMap.put("/listMapMamageNoPage", "perms[listMapMamageNoPage]");
        filterChainDefinitionMap.put("/listMapMamageNoPage2", "perms[listMapMamageNoPage2]");
        filterChainDefinitionMap.put("/listMapMamage", "perms[listMapMamage]");
        filterChainDefinitionMap.put("/deleteMapMamage", "perms[deleteMapMamage]");
        filterChainDefinitionMap.put("/updateMapMamage", "perms[updateMapMamage]");
        filterChainDefinitionMap.put("/addMapMamage", "perms[addMapMamage]");
        filterChainDefinitionMap.put("/listMapMamageSearch", "perms[listMapMamageSearch]");
        filterChainDefinitionMap.put("/listMapMamageSearchByIndoorname", "perms[listMapMamageSearchByIndoorname]");

        filterChainDefinitionMap.put("/updateStatus", "perms[updateStatus]");
        filterChainDefinitionMap.put("/updateStatus2", "perms[updateStatus2]");
        filterChainDefinitionMap.put("/doStopVisit", "perms[doStopVisit]");
        filterChainDefinitionMap.put("/getcontrol", "perms[getcontrol]");
        filterChainDefinitionMap.put("/deleteAllCach", "perms[deleteAllCach]");
        filterChainDefinitionMap.put("/listUserByRoleNoPage", "perms[listUserByRoleNoPage]");
        filterChainDefinitionMap.put("/getSumData", "perms[getSumData]");
        filterChainDefinitionMap.put("/mapByMac", "perms[mapByMac]");

        filterChainDefinitionMap.put("/**", "url");
        shiroFilterFactoryBean.setFilters(customisedFilter);
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }


    public URLPathMatchingFilter getURLPathMatchingFilter() {
        return new URLPathMatchingFilter();
    }

    /**
     * 配置LogoutFilter
     * @return
     */
    public ShiroLogoutFilter shiroLogoutFilter(){
        ShiroLogoutFilter shiroLogoutFilter = new ShiroLogoutFilter();
        return shiroLogoutFilter;
    }


    @Bean
    public SecurityManager securityManager(){
        DefaultWebSecurityManager securityManager =  new DefaultWebSecurityManager();
        //设置realm.
        securityManager.setRealm(getDatabaseRealm());
        return securityManager;
    }

    @Bean
    public DatabaseRealm getDatabaseRealm(){
        DatabaseRealm myShiroRealm = new DatabaseRealm();
        myShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return myShiroRealm;
    }

    /**
     * 凭证匹配器
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(2);
        return hashedCredentialsMatcher;
    }

    /**
     *  开启shiro aop注解支持.
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }
}
