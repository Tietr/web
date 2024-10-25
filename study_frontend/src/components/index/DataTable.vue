<script setup>
import {Search, Timer} from "@element-plus/icons-vue";
import {reactive, ref, onMounted} from "vue";
import {get, getByParam, post} from "@/net/index.js";
import {ElMessage} from "element-plus";

// 存储所有用户数据
const tableDataAll = reactive([]);
// 当前页显示的数据
const tableData = reactive([]);

const userForm = reactive({
  date: '',
  name: '',
  address: '',
  email:'',
  oldEmail:''
});

const searchName =ref('');
const dialogVisible = ref(false);
const editIndex = ref(null);
const isEditMode = ref(false);

// 分页参数
const currentPage = ref(1); // 当前页
const pageSize = ref(5);    // 每页显示多少条数据
const TotalPage = ref(0);

const handleAdd=()=>{
  isEditMode.value = false
  userForm.date = new Date().toISOString().split("T")[0];
  userForm.name = '';
  userForm.address = '';
  userForm.email = '';
  dialogVisible.value = true
}

const handleEdit=(index,row)=>{
  isEditMode.value = true
  editIndex.value = index
  userForm.date = row.date;
  userForm.name = row.name;
  userForm.address = row.address;
  userForm.email = row.email;
  userForm.oldEmail = row.email;
  dialogVisible.value = true
}

const saveData =()=>{
  dialogVisible.value = false
  if(isEditMode.value === false){
  post("api/data/post",{
    email: userForm.email,
    date: userForm.date,
    name: userForm.name,
    address: userForm.address,
  },(message)=>{
    getAllData();
    updateTableData();
    ElMessage.success(message);
  })
  }else if(isEditMode.value === true){
    post("api/data/put",{
      email: userForm.email,
      date: userForm.date,
      name: userForm.name,
      address: userForm.address,
      oldEmail: userForm.oldEmail,
    },(message)=>{
      getAllData();
      updateTableData();
      ElMessage.success(message);
    })
  }
}

const handleDelete =(index,row)=>{
  post("api/data/del",{
    email:row.email
  },(message)=>{
    getAllData();
    updateTableData();
    ElMessage.success(message)
  })
}

const getAllData=()=>{
  get('api/data/get',
      (message)=>{
        TotalPage.value = Math.ceil( message.length/pageSize.value);
        // console.log('length:',TotalPage.value);
        tableDataAll.splice(0, tableDataAll.length);
        tableDataAll.push(...message);
        updateTableData();
        // ElMessage.success(`Data successfully fetched: ${JSON.stringify(tableDataAll)}`);
      }
  )
}
const updateTableData = () =>{
  const startIndex = (currentPage.value-1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  tableData.splice(0,tableData.length);
  tableData.push(...tableDataAll.slice(startIndex,endIndex));
}
const handleCurrentChange = (page) => {
  currentPage.value = page;
  updateTableData();
};

const searchByName = ()=>{
  if(searchName.value === ''){
    getAllData();
  }else {
    getByParam("api/data/get-by-name",{
      name:searchName.value
    },(message)=>{
      TotalPage.value = Math.ceil( message.length/pageSize.value);
      tableDataAll.splice(0, tableDataAll.length)
      tableDataAll.push(...message);
      updateTableData();
    })
  }

}
const ValidDate = (rule,value,callback)=>{
  if (value===''){
    callback(new Error('日期不能为空'));
  }else if(!/^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$/.test(value)){
    callback(new Error('YYYY-MM-DD'));
  }else{
    callback()
  }
}

const rules = {
  date:[
    {validator: ValidDate,trigger:['blur','change']}
  ],
  email: [
    {required: true, message: '请输入邮箱地址', trigger:['blur', 'change'],},
    {type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'],}
  ]
}


onMounted(getAllData);
</script>

<template>

  <div style="display: inline;float: left;">
    <el-input v-model="searchName" style="width: 240px;" placeholder="Please input" />
    <el-button style="margin-left: 5px" :icon="Search" @click="searchByName"/>
  </div>

  <div style="margin-bottom: 20px;display: inline; float: right;">
    <el-button type="primary" @click="handleAdd">Add New</el-button>
  </div>

  <!-- 表格区域 -->
  <el-table
      :data="tableData"
      style="width: 100%; min-height: 300px"  empty-text=" ">
  <el-table-column label="Date" width="180">
    <template #default="scope">
      <div style="display: flex; align-items: center">
        <el-icon>
          <timer/>
        </el-icon>
        <span style="margin-left: 10px">{{ scope.row.date }}</span>
      </div>
    </template>
  </el-table-column>
  <el-table-column label="Name" width="180">
    <template #default="scope">
      <el-tag>{{ scope.row.name }}</el-tag>
    </template>
  </el-table-column>
  <el-table-column label="Address" width="180">
    <template #default="scope">
      <span>{{ scope.row.address }}</span>
    </template>
  </el-table-column>
    <el-table-column label="email" width="180">
      <template #default="scope">
        <span>{{ scope.row.email }}</span>
      </template>
    </el-table-column>
  <el-table-column label="Operations">
    <template #default="scope">
      <el-button size="small" type="primary" @click="handleEdit(scope.$index, scope.row)">
        Edit
      </el-button>
      <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)" style="margin-left: 5px">
        Delete
      </el-button>
    </template>
  </el-table-column>
  </el-table>

  <!-- 分页组件 -->
  <el-pagination
      background
      layout="prev, pager, next"
      :current-page="currentPage"
      :page-size="pageSize"
      :page-count="TotalPage"
      @current-change="handleCurrentChange"
      style="margin-top: 20px; text-align: center;"
  />


  <!-- 编辑和新增对话框 -->
  <el-dialog :title="isEditMode ? 'Edit User' : 'Add New User'" v-model="dialogVisible" width="30%">
    <el-form :model="userForm" :rules="rules">
      <el-form-item label="Date" prop="date">
        <el-input v-model="userForm.date"></el-input>
      </el-form-item>
      <el-form-item label="Name">
        <el-input v-model="userForm.name"></el-input>
      </el-form-item>
      <el-form-item label="Address">
        <el-input v-model="userForm.address"></el-input>
      </el-form-item>
      <el-form-item label="email" prop="email">
        <el-input v-model="userForm.email"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">Cancel</el-button>
      <el-button type="primary" @click="saveData">Save</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.el-button {
  font-weight: bold;
}

.el-table {
  /* 设置表格的最小高度 */
  min-height: 300px;
}

.el-table th {
  background-color: #f5f7fa;
  color: #409EFF;
}

.el-dialog .el-input {
  margin-bottom: 15px;
}
</style>
