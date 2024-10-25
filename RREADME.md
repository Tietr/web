# 索引表

1. [项目概述](#项目概述)
2. [实现所用技术](#实现所用技术)
3. [PSP表格](#psp表格)
4. [服务器部署](#服务器部署)
5. [成品展示](#成品展示)
6. [代码解释](#代码解释)
    - [后端](#后端)
        - [AuthorizeController - 用户注册](#1-authorizecontroller---用户注册)
        - [MemberController - 增删改查](#2-membercontroller---增删改查)
        - [UserController - 拦截器用户识别](#3-usercontroller---拦截器用户识别)
        - [AuthorizeInterceptor - 拦截器](#4-authorizeinterceptor---拦截器)
        - [RestBean - 前端交互类](#5-restbean---前端交互类)
        - [Mapper - 数据库操作](#6-mapper---数据库操作)
    - [前端](#前端)
        - [net.index.js - 后端交互](#1-netindexjs---后端交互)
        - [router.index.js - 界面层级](#2-routerindexjs---界面层级)
7. [个人学习历程](#个人学习历程)
    - [目标](#目标)
    - [技术栈学习](#技术栈学习)
    - [后端开发](#后端开发)
    - [前端开发](#前端开发)
    - [功能测试](#功能测试)
    - [总结](#总结)

---

# 项目概述
## 前后端分离-练习项目
* 登录功能（支持用户名、邮箱登录）
* 注册用户（通过邮箱注册）
* 重置密码（通过邮箱重置密码）
* 基本的增、删、改、查

# 实现所用技术
- SpringBoot 后端框架
- mysql 数据库
- [Redis-win](https://github.com/tporadowski/redis.git) 持久化存储 
- VUE.js 前端框架
- 使用element plus 前端组件库

# PSP table
 | 任务阶段 |            任务内容             | 预估耗时 | 实际耗时 |
 |:----:|:---------------------------:|:----:|:----:|
 | 需求分析 |        前后端分离的增删改查练习         |30min |30min |
 |数据库设计 |         用户信息格式-联系方式         |  30min  | 30min |
 |后端开发|  基于spring boot 框架，向前端提供接口   |20h |20h|
 |前端开发 | 基于VUE.js elment-plusw前端快速开发 |10h |10h|
 |测试|     测试网站功能是否符合预期-未考虑安全性     |2h |2h|

# 服务器部署
出于成本和安全性考虑，并没有部署上线，但是你可以使用[docker desktop](https://www.docker.com/)运行，请到达该项目的根目录处运行指令
```cmd/powershell
docker-compose up --build
```
如果构建时报错，应该是网络问题，请使用代理工具或自行添加国内镜像
# 成品展示 -- 本地环境
[video.mp4](video.mp4)
# 代码解释
## 后端 
1. AuthorizeController -> 用户注册类
 ```java
 @Validated
 @RestController
 @RequestMapping("/api/auth")//请求相关
 public class AuthorizeController {
  private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$";
  private final String USERNAME_REGEX = "[一-龥a-zA-Z0-9]+";
  private final String PASSWORD_REGEX = "^[^一-龥]+$";
  @Resource
  AuthorizeService authorizeService;
  @PostMapping("/valid-register-email")
  public RestBean<String> validateRegisterEmail(@Pattern(regexp=EMAIL_REGEX) @RequestParam("email") String name,HttpSession session) {
   String s = authorizeService.sendValidateEmail(name,session.getId(),false);
   if(s == null){
    return RestBean.success("邮件已发送，请注意查收");
   }else {
    return RestBean.failure(400,s);
   }
  }
  @PostMapping("/valid-reset-email")
  public RestBean<String> validateResetEmail(@Pattern(regexp=EMAIL_REGEX) @RequestParam("email") String name,HttpSession session) {
   String s = authorizeService.sendValidateEmail(name,session.getId(),true);
   if(s == null){
    return RestBean.success("邮件已发送，请注意查收");
   }else {
    return RestBean.failure(400,s);
   }
  }
  @PostMapping("/register")
  public RestBean<String> registerUser(
          @Pattern(regexp= USERNAME_REGEX) @Length(min = 2,max = 8) @RequestParam("username") String username,
          @Pattern(regexp = PASSWORD_REGEX) @Length(min = 6,max = 20) @RequestParam("password") String password,
          @Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
          @Length(min = 6,max = 6) @RequestParam("code") String code,
          HttpSession session) {
   String s = authorizeService.validateAndRegister(username,password,email,code,session.getId());
   if (s == null) {
    return RestBean.success("用户注册成功");
   }else {
    return RestBean.failure(400,s);
   }
  }
  @PostMapping("/start-reset")
  public RestBean<String> startReset(
          @Pattern(regexp = EMAIL_REGEX) @RequestParam("email") String email,
          @Length(min = 6,max = 6) @RequestParam("code") String code,
          HttpSession session
  ){
   String s = authorizeService.validateOnly(email,code,session.getId());
   if(s == null){
    session.setAttribute("reset-password",email);
    return RestBean.success();
   }else {
    return RestBean.failure(400,s);
   }
  }
  @PostMapping("/do-reset")
  public RestBean<String> restPassword(
          @Pattern(regexp = PASSWORD_REGEX) @Length(min = 6,max = 20) @RequestParam("password") String password,
          HttpSession session
  ){
   String email = (String)session.getAttribute("reset-password");
   if (email == null) {
    return RestBean.failure(401,"请先完成邮箱验证");
   }else if(authorizeService.resetPassword(email,password)){
    session.removeAttribute("reset-password");
    return RestBean.success("密码重置成功");
   }else {
    return RestBean.failure(500,"内部错误，请联系管理员");
   }
  }
 }
 ```
2. MemberController -> 增删改查类
```java

@RestController
@RequestMapping("/api/data")
public class MemberController {
    //具体实现
    @Resource
    TeamMemberService teamMemberService;
    //增
    @PostMapping("/post")
    public RestBean<String> postNewData(
        @Valid @ModelAttribute TeamMember teamMember
    ){
        TeamMember member = teamMemberService.getTeamMemberByEmail(teamMember.getEmail());
        if(member == null){
            teamMemberService.addTeamMember(teamMember);
            return RestBean.success("添加成功");
        }else{
            return RestBean.failure(300,"该邮箱已有成员拥有");
        }
    }
     //删
    @PostMapping("/del")
    public RestBean<String> deleteData(
            @Valid @RequestParam("email") String email
    ){
        TeamMember member = teamMemberService.getTeamMemberByEmail(email);
        if(member == null){
            return RestBean.failure(301,"该成员不存在");
        }else{
            teamMemberService.deleteTeamMember(member);
            return RestBean.success("删除成功");
        }
    }
    //改
    @PostMapping("/put")
    public RestBean<String> putData(
            @Valid @ModelAttribute TeamMember Data
    ){
        if(!teamMemberService.updateMemberDetail(Data)){
            return RestBean.failure(302,"修改的成员不存在");
        }else{
            return RestBean.success("修改成功");
        }
    }
    //查
    @GetMapping("/get")
    public RestBean<List<TeamMember>> getDataAll(){
        return RestBean.success(teamMemberService.getTeamMember());
    }
    @GetMapping("get-by-name")
    public RestBean<List<TeamMember>> getDataByName(
            @RequestParam("name") String name
    ){
        List<TeamMember> members = teamMemberService.getTeamMemberByName(name);
        return RestBean.success(members);
    }
}
```
3. UserController -> 拦截器用户识别类
```java
@RestController
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public RestBean<AccountUser> me(@SessionAttribute("account") AccountUser user){
        return RestBean.success(user);
    }
}
```
4. AuthorizeInterceptor -> 拦截器
```java
@Component
public class AuthorizeInterceptor implements HandlerInterceptor {
    @Resource
    UserMapper mapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        User user = (User)authentication.getPrincipal();
        String username = user.getUsername();
        AccountUser account = mapper.findAccountUserByNameOrEmail(username);
        request.getSession().setAttribute("account", account);
        return true;
    }
}
```
5. RestBean前端交互类
```java
@Data
public class RestBean<T> {
    private int status;
    private boolean success;
    private T message;

    public RestBean(int status, boolean success, T message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public static <T> RestBean<T> success(T data) {
        return new RestBean<>(200,true,data);
    }
    public static <T> RestBean<T> success() {
        return  new RestBean<>(200,true,null);
    }

    public static <T> RestBean<T> failure(int status) {
        return new RestBean<>(status,false,null);
    }
    public static <T> RestBean<T> failure(int status, T data) {
        return new RestBean<>(status,false,data);
    }
}
```
6. Mapper->数据库操作
```java
//增删改查
@Mapper
public interface MemberMapper {
    //查全部
    @Select("select * from team_member")
    List<TeamMember> findAll();
    //根据名字查
    @Select("select * from team_member where name like CONCAT('%', #{name}, '%')")
    List<TeamMember> findByName(String name);

    @Select("select * from team_member where email =#{email}")
    TeamMember findByEmail(String email);
    //增
    @Insert("insert into team_member (name,address,date,email) values (#{name},#{address},#{date},#{email})")
    int insert(String name,String address,String date,String email);
    //改
    @Update("update team_member set name=#{newName},address=#{newAddress},date=#{newDate},email=#{newEmail} where email =#{oldEmail}")
    int update(String oldEmail,String newName,String newAddress,String newDate,String newEmail);
    //删
    @Delete("delete from team_member where email=#{email}")
    int delete(String email);
}
//用户注册
@Mapper
public interface UserMapper {
 @Select("select * from db_account where username = #{text} or email = #{text}")
 Account findAccountByNameOrEmail(String text);

 @Select("select * from db_account where username = #{text} or email = #{text}")
 AccountUser findAccountUserByNameOrEmail(String text);

 @Insert("insert into db_account (email,username,password) values (#{email},#{username},#{password})")
 int createAccount(String username, String password, String email);

 @Update("update db_account set password = #{password} where email = #{email}")
 int resetPasswordByEmail(String email, String password);
}
```
## 前端
1. net.index.js-> 后端交互
````js
const defaultError = ()=> ElMessage.error('发生了一些错误，请联系管理员')
const defaultFailure = (message)=> ElMessage.warning(message)

function post(url,data,success,failure = defaultFailure, error = defaultError) {
 axios.post(url,data,{
  headers:{
   'Content-Type': 'application/x-www-form-urlencoded'
  },
  withCredentials:true
 }).then(({data}) => {
  if (data.success) {
   success(data.message,data.status)
  }else{
   failure(data.message,data.status)
  }
 }).catch(error)
}
function get(url,success,failure = defaultFailure,error = defaultError) {
 axios.get(url,{
  withCredentials:true
 }).then(({data}) => {
  if (data.success) {
   success(data.message,data.status);
  }else{
   failure(data.message,data.status);
  }
 }).catch(error)
}
function getByParam(url, params, success, failure = defaultFailure, error = defaultError) {
 axios.get(url, {
  params: params, // 通过 params 传递查询参数
  withCredentials: true
 }).then(({data}) => {
  if (data.success) {
   success(data.message, data.status);
  } else {
   failure(data.message, data.status);
  }
 }).catch(error);
}
export {get,post,getByParam};
````
2. router.index.js -> 界面层级
```js
const router = createRouter({
 history: createWebHistory(import.meta.env.BASE_URL),
 routes: [
  {
   path: '/',
   name: 'welcome',
   component: () => import('@/views/WelcomeView.vue'),
   children: [
    {
     path: '',
     name:'welcome-login',
     component:()=>import('@/components/welcome/LoginPage.vue')
    },{
     path:'register',
     name:'welcome-register',
     component:()=>import('@/components/welcome/RegisterPage.vue')
    },{
     path:'forget',
     name:'welcome-forget',
     component:()=>import('@/components/welcome/ForgetPage.vue')
    }
   ]
  },{
   path: '/index',
   name: 'index',
   component:()=> import('@/views/IndexView.vue'),
   children: [{
    path: '',
    name: '',
    component:()=>import('@/components/index/DataTable.vue')
   }]
  }
 ]
})
router.beforeEach((to, from, next) => {
 const store = useStore()
 if(store.auth.user != null && to.name.startsWith('welcome-')) {
  next('/index')
 } else if(store.auth.user == null && to.fullPath.startsWith('/index')) {
  next('/')
 } else if(to.matched.length === 0){
  next('/index')
 } else {
  next()
 }
})
export default router
```

# 个人学习历程
## 目标：
掌握使用SpringBoot和Vue实现前后端分离的增删改查（CRUD）操作。
## 技术栈学习
1. SpringBoot基础：
   学习SpringBoot框架，包括其核心概念、依赖注入、注解等。
2. Vue.js入门：
   熟悉Vue.js的基本语法、组件化开发以及状态管理（Vuex）。
## 后端开发
1. 搭建SpringBoot项目：
   使用SpringInitializr快速搭建项目，并配置数据库连接。
2. 数据库操作：
   使用SpringDataJPA或MyBatis与数据库交互，实现数据的持久化。
## 前端开发
1. 搭建Vue项目：
   使用Vue CLI创建项目，并配置所需的开发环境。
2. 前端界面开发：
   开发用户友好的界面，实现用户数据的展示、添加、编辑和删除。
3. 前后端数据交互：
   使用Axios库在Vue中发起HTTP请求，与后端API进行数据交互。
## 功能测试
1. 单元测试：
   编写单元测试，确保前后端逻辑的正确性。
2. 集成测试：
   进行端到端测试，确保整个CRUD流程的流畅性。
## 总结：
通过本次学习，我成功掌握了使用SpringBoot和Vue实现前后端分离的CRUD操作。虽然没有进行项目部署，但我已经具备了将应用部署到生产环境的能力。
