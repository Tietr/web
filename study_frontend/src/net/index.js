import axios from "axios";
import {ElMessage} from "element-plus";

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

function postFile(url, data, success, failure = defaultFailure, error = defaultError) {
    axios.post(url, data, {
        headers: {
            'Content-Type': 'multipart/form-data',
        },
        withCredentials: true,
    }).then(({data}) => {
        if (data.success) {
            success(data.message, data.status);
        } else {
            failure(data.message, data.status);
        }
    }).catch(error);
}

function getFile(url, success, failure = defaultFailure, error = defaultError) {
    axios.get(url, {
        responseType: 'arraybuffer',  // 设置响应类型为 arraybuffer 以便处理二进制文件数据
        withCredentials: true,        // 可选，是否需要跨域时携带 cookies
    }).then(({ data }) => {
        // 如果返回的是文件（arraybuffer 类型），可以手动触发文件下载
        if (data instanceof ArrayBuffer) {
            const blob = new Blob([data], {
                type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet', // 根据文件类型设置 MIME 类型
            });
            const link = document.createElement('a');
            link.href = URL.createObjectURL(blob);
            link.download = 'members.xlsx';  // 设置下载的文件名
            link.click();
            URL.revokeObjectURL(link.href); // 释放 URL 对象
            ElMessage.success('文件导出成功！');
        } else {
            // 如果不是文件，而是正常的数据（如 JSON），处理消息
            if (data.success) {
                success(data.message, data.status);
            } else {
                failure(data.message, data.status);
            }
        }
    }).catch(error);
}

export {get,post,getByParam,postFile,getFile};