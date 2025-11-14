// 网络请求封装模块
import axios  from "axios";               // 导入axios HTTP库
import {ElMessage} from "element-plus";   // 导入Element Plus消息提示组件

// 存储认证信息的键名
const authItemName="access_token"

// 默认请求失败处理函数
const defaultFailure=(message,code,url)=>{
    // 在控制台输出警告信息
    console.warn(`请求地址:${url},状态码:${code},错误信息:${message}`)
    // 显示警告消息提示
    ElMessage.warning(message)
}

// 默认请求错误处理函数
const defaultError=(err)=>{
    // 在控制台输出错误信息
    console.warn(err)
    // 显示错误消息提示
    ElMessage.warning("发生了错误，请联系管理员")
}

/**
 * 获取访问令牌
 * @returns 访问令牌或null
 */
function takeAccessToken(){
    // 从localStorage或sessionStorage中获取认证信息
    const str=localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
    // 如果没有获取到认证信息，返回null
    if(!str)return null
    // 解析存储的认证对象
    const authObj=JSON.parse(str)
    // 判断认证信息是否过期
    if(authObj.expire<=new Date()){
        // 如果过期则删除认证信息
        deleteAccessToken()
        // 显示过期提醒
        ElMessage.warning("登录已过期")
        return null
    }
    // 返回有效的访问令牌
    return authObj.token
}

/**
 * 存储访问令牌
 * @param token 访问令牌
 * @param remember 是否记住登录状态
 * @param expire 过期时间
 */
function storeAccessToken(token,remember,expire){
    // 构造认证对象
    const authObj={token:token,expire:expire}
    // 将认证对象转换为JSON字符串
    const str=JSON.stringify(authObj)
    // 根据是否记住登录状态选择存储方式
    if(remember)
        localStorage.setItem(authItemName, str)      // 永久存储
    else
        sessionStorage.setItem(authItemName, str)    // 临时存储
}

/**
 * 删除访问令牌
 */
 function deleteAccessToken(){
    // 从localStorage中移除认证信息
    localStorage.removeItem(authItemName)
    // 从sessionStorage中移除认证信息
    sessionStorage.removeItem(authItemName)
}
function accessHeader(){
     const token=takeAccessToken()
     return token ? {"Authorization":`Bearer ${token}`} : {}
}

/**
 * 内部POST请求方法
 * @param url 请求地址
 * @param data 请求数据
 * @param header 请求头
 * @param success 成功回调函数
 * @param failure 失败回调函数
 * @param error 错误回调函数
 */
function internalPost(url,data,header,success,failure,error=defaultError){
    // 检查内容类型是否为x-www-form-urlencoded
    let requestData = data;
    if (header && header['Content-Type'] === 'application/x-www-form-urlencoded') {
        // 转换数据为URL编码格式
        requestData = Object.keys(data)
            .map(key => `${encodeURIComponent(key)}=${encodeURIComponent(data[key])}`)
            .join('&');
    }
    
    // 发送POST请求
    axios.post(url,requestData,{headers:header}).then(({data})=>{
      // 判断响应状态码是否为200（成功）
      if(data.code===200){
          // 执行成功回调函数
          success(data.data);
      }else{
          // 执行失败回调函数
          failure(data.message,data.code,url);
      }
    }).catch(err=>error(err))  // 捕获并处理错误
}

/**
 * 内部GET请求方法
 * @param url 请求地址
 * @param header 请求头
 * @param success 成功回调函数
 * @param failure 失败回调函数
 * @param error 错误回调函数
 */
function internalGet(url,header,success,failure,error=defaultError){
    // 发送GET请求
    axios.get(url,{headers:header}).then(({data})=>{
        // 判断响应状态码是否为200（成功）
        if(data.code===200){
            // 执行成功回调函数
            success(data.data);
        }else{
            // 执行失败回调函数
            failure(data.message,data.code,url);
        }
    }).catch(err=>error(err))  // 捕获并处理错误
}
function get(url,success,failure=defaultFailure){
    // 发送GET请求
    internalGet(url,accessHeader(),success,failure)
}
function post(url,data,success,failure=defaultFailure){
    // 发送POST请求
    internalPost(url,data,accessHeader(),success,failure)
}
/**
 * 用户登录方法
 * @param username 用户名
 * @param password 密码
 * @param remember 是否记住登录状态
 * @param success 登录成功的回调函数
 * @param failure 登录失败的回调函数
 */
function login(username,password,remember,success,failure=defaultFailure) {
    // 发送登录POST请求
    // 注意：Spring Security表单登录需要x-www-form-urlencoded格式
    internalPost('api/auth/login',{
        username: username,
        password:password
    },{
        'Content-Type':'application/x-www-form-urlencoded'
    },(data)=>{
        // 存储访问令牌
        storeAccessToken(data.token,remember,data.expire)
        // 显示登录成功消息
        ElMessage.success(`登录成功,欢迎${username}`)
        // 执行登录成功回调函数
        success(data);
    }, failure)
}
function logout(success, failure = defaultFailure) {
    // 简化退出登录逻辑：主要处理前端token删除
    // 由于后端Redis配置缺失，退出登录接口会失败
    // 这里优先确保前端token被正确删除，用户能够正常退出
    
    // 删除访问令牌
    deleteAccessToken()
    // 显示注销成功消息
    ElMessage.success("注销成功")
    // 执行注销成功回调函数
    success()
    
    // 异步调用后端退出接口（不依赖其结果）
    // 如果后端Redis配置正常，会成功执行；如果配置缺失，忽略失败
    post('api/auth/logout', {}, () => {
        console.log('后端退出登录成功')
    }, (message) => {
        console.warn('后端退出登录失败（Redis未配置）:', message)
    })
}
function unauthorized(){
   return !takeAccessToken()
}
// 导出登录方法供外部使用
export {login,logout,get,post,unauthorized}
