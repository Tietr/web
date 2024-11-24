<script setup>
import { Search, Timer, Upload } from "@element-plus/icons-vue";
import { reactive, ref, onMounted } from "vue";
import {get, getByParam, getFile, post} from "@/net/index.js";
import { ElMessage, ElMessageBox } from "element-plus";

const tableDataAll = reactive([]); // 存储所有用户数据
const tableData = reactive([]); // 当前页显示的数据

const userForm = reactive({
  date: "",
  name: "",
  address: "",
  email: "",
  oldEmail: "",
  star: false, // 新增 star 属性
});

const searchName = ref("");
const dialogVisible = ref(false);
const editIndex = ref(null);
const isEditMode = ref(false);

// 分页参数
const currentPage = ref(1); // 当前页
const pageSize = ref(5); // 每页显示多少条数据
const TotalPage = ref(0);

// 打开新增对话框
const handleAdd = () => {
  isEditMode.value = false;
  userForm.date = new Date().toISOString().split("T")[0];
  userForm.name = "";
  userForm.address = "";
  userForm.email = "";
  userForm.star = false; // 默认不勾选 Star
  dialogVisible.value = true;
};

// 打开编辑对话框
const handleEdit = (index, row) => {
  isEditMode.value = true;
  editIndex.value = index;
  userForm.date = row.date;
  userForm.name = row.name;
  userForm.address = row.address;
  userForm.email = row.email;
  userForm.oldEmail = row.email;
  userForm.star = row.star; // 设置 star 状态
  dialogVisible.value = true;
};

// 保存数据（新增或编辑）
const saveData = () => {
  dialogVisible.value = false;
  if (!isEditMode.value) {
    post(
        "api/data/post",
        {
          email: userForm.email,
          date: userForm.date,
          name: userForm.name,
          address: userForm.address,
          star: userForm.star,
        },
        (message) => {
          getAllData();
          updateTableData();
          ElMessage.success(message);
        }
    );
  } else {
    post(
        "api/data/put",
        {
          email: userForm.email,
          date: userForm.date,
          name: userForm.name,
          address: userForm.address,
          oldEmail: userForm.oldEmail,
          star: userForm.star,
        },
        (message) => {
          getAllData();
          updateTableData();
          ElMessage.success(message);
        }
    );
  }
};

// 删除数据
const handleDelete = (index, row) => {
  ElMessageBox.confirm("确定要删除该成员吗？", "提示", {
    type: "warning",
  }).then(() => {
    post(
        "api/data/del",
        {
          email: row.email,
        },
        (message) => {
          getAllData();
          updateTableData();
          ElMessage.success(message);
        }
    );
  });
};

// 获取所有数据
const getAllData = () => {
  get("api/data/get", (message) => {
    TotalPage.value = Math.ceil(message.length / pageSize.value);
    tableDataAll.splice(0, tableDataAll.length);
    tableDataAll.push(...message);
    updateTableData();
  });
};

// 更新表格数据
const updateTableData = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  tableData.splice(0, tableData.length);
  tableData.push(...tableDataAll.slice(startIndex, endIndex));
};

// 分页切换
const handleCurrentChange = (page) => {
  currentPage.value = page;
  updateTableData();
};

// 按名字搜索
const searchByName = () => {
  if (searchName.value === "") {
    getAllData();
  } else {
    getByParam(
        "api/data/get-by-name",
        {
          name: searchName.value,
        },
        (message) => {
          TotalPage.value = Math.ceil(message.length / pageSize.value);
          tableDataAll.splice(0, tableDataAll.length);
          tableDataAll.push(...message);
          updateTableData();
        }
    );
  }
};

// 导入 Excel 文件 - 未完成
const handleImport = (file) => {
  const formData = new FormData();
  formData.append("file", file.value.raw);
  postFile(
      "api/data/import",
      formData,
      (message) => {
        getAllData();
        ElMessage.success(message);
      }
  );
};

// 导出 Excel 文件 - 未完成
const handleExport = () => {
  getFile("api/data/export", (data) => {

  });
};

const switchValue =ref(false);
const handleSwitchChange = ()=>{
  if (switchValue.value) {
    get(
        "api/data/get-by-star",
        (message) => {
          TotalPage.value = Math.ceil(message.length / pageSize.value);
          tableDataAll.splice(0, tableDataAll.length);
          tableDataAll.push(...message);
          updateTableData();
        }
    )
  }else {
    getAllData();
  }
}

const phoneDialogVisible = ref(false);

const phoneForm = reactive({
  email: "", // 当前用户的 email
  phones: [], // 电话号码列表
});

// 打开对话框并加载电话号码
const openPhoneDialog = (email) => {
  phoneDialogVisible.value = true;
  phoneForm.email = email; // 设置当前用户的 email
  phoneForm.phones = [];

  getByParam(
      "api/data/get-phone",
      { email: email },
      (phones) => {
        phoneForm.phones.push(...phones); // 加载数据到表单
      },
      (error) => {
        ElMessage.error("Failed to load phone data: " + error.message);
      }
  );
};

// 添加一个新的电话号码输入框并同步到后端
const addPhone = () => {
  const newPhone = ""; // 新的空电话号码
  phoneForm.phones.push(newPhone);

  // 立即同步到后端
  post(
      "api/data/post-phone",
      { email: phoneForm.email, phone: newPhone },
      () => {
        ElMessage.success("New phone added successfully.");
      },
      (error) => {
        ElMessage.error("Failed to add new phone: " + error.message);
      }
  );
};

// 删除电话号码并同步到后端
const removePhone = (index) => {
  ElMessage.success(phoneForm.email);
  ElMessage.success(phoneForm.phones[index]);
  const phone = phoneForm.phones[index];
  if (phone) {
    post(
        "api/data/del-phone",
        { email: phoneForm.email, phone },
        () => {
          ElMessage.success(`Phone ${phone} deleted successfully.`);
        },
        (error) => {
          ElMessage.error("Failed to delete phone: " + error.message);
        }
    );
  }
  phoneForm.phones.splice(index, 1); // 从本地表单中移除
};

// 编辑电话号码时，实时更新后端
const updatePhone = (index, newPhone) => {
  const oldPhone = phoneForm.phones[index];

  // 如果没有变更，不调用后端
  if (oldPhone === newPhone) return;

  // 调用后端接口进行更新
  post(
      "api/data/put-phone",
      { email: phoneForm.email, oldPhone, newPhone },
      () => {
        ElMessage.success(`Phone ${oldPhone} updated to ${newPhone} successfully.`);
        phoneForm.phones[index] = newPhone; // 本地更新
      },
      (error) => {
        ElMessage.error("Failed to update phone: " + error.message);
      }
  );
};

onMounted(getAllData);
</script>

<template>
  <div style="display: inline; float: left;">
    <el-input v-model="searchName" style="width: 240px;" placeholder="Search by Name" />
    <el-button style="margin-left: 5px" :icon="Search" @click="searchByName" />
  </div>
  <div class = "star_member">
    <el-switch v-model="switchValue" @change="handleSwitchChange" />
  </div>

  <div style="margin-bottom: 20px; display: inline; float: right;">
    <el-upload
        :http-request="handleImport"
        accept=".xlsx, .xls"
        show-file-list="false">
      <el-button type="success"  style="margin-left: 5px;">Import</el-button>
    </el-upload>
    <el-button type="success" @click="handleExport" style="margin-left: 5px;">Export</el-button>
    <el-button type="primary" @click="handleAdd" style="margin-left: 10px;">Add New</el-button>
  </div>

  <!-- 表格区域 -->
  <el-table :data="tableData" style="width: 100%; min-height: 300px" empty-text="No Data">
    <el-table-column label="Date" width="180">
      <template #default="scope">
        <div style="display: flex; align-items: center">
          <el-icon>
            <Timer />
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
    <el-table-column label="Email" width="180">
      <template #default="scope">
        <span>{{ scope.row.email }}</span>
      </template>
    </el-table-column>
    <el-table-column label="Star" width="100">
      <template #default="scope">
        <el-switch v-model="scope.row.star" disabled />
      </template>
    </el-table-column>
    <el-table-column label="Operations">
      <template #default="scope">
        <el-button size="small" type="primary" @click="handleEdit(scope.$index, scope.row)">
          Edit
        </el-button>
        <el-button size="small" type="danger" @click="handleDelete(scope.$index, scope.row)" style="margin-left: 5px;">
          Delete
        </el-button>
        <el-button size="small" @click="openPhoneDialog(scope.row.email)">Edit Phones</el-button>
      </template>
    </el-table-column>
  </el-table>

  <!-- 分页 -->
  <el-pagination
      background
      layout="prev, pager, next"
      :current-page="currentPage"
      :page-size="pageSize"
      :page-count="TotalPage"
      @current-change="handleCurrentChange"
      style="margin-top: 20px; text-align: center;"
  />

  <!-- 对话框 -->
  <el-dialog :title="isEditMode ? 'Edit User' : 'Add New User'" v-model="dialogVisible" width="30%">
    <el-form :model="userForm">
      <el-form-item label="Date">
        <el-input v-model="userForm.date"></el-input>
      </el-form-item>
      <el-form-item label="Name">
        <el-input v-model="userForm.name"></el-input>
      </el-form-item>
      <el-form-item label="Address">
        <el-input v-model="userForm.address"></el-input>
      </el-form-item>
      <el-form-item label="Email">
        <el-input v-model="userForm.email"></el-input>
      </el-form-item>
      <el-form-item label="Star">
        <el-switch v-model="userForm.star"></el-switch>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">Cancel</el-button>
      <el-button type="primary" @click="saveData">Save</el-button>
    </template>
  </el-dialog>

  <!-- 编辑或添加手机号码对话框 -->
  <el-dialog :title="'Edit Phones'" v-model="phoneDialogVisible" width="30%">
    <el-form :model="phoneForm">
      <el-form-item label="Phones">
        <div
            v-for="(phone, index) in phoneForm.phones"
            :key="index"
            style="display: flex; align-items: center; margin-bottom: 10px;"
        >
          <!-- 输入框，实时更新电话号码 -->
          <el-input
              v-model="phoneForm.phones[index]"
              placeholder="Enter phone number"
              @blur="updatePhone(index, phoneForm.phones[index])"
              style="flex: 1;"
          ></el-input>

          <!-- 删除按钮 -->
          <el-button
              icon="Delete"
              type="danger"
              size="small"
              @click="removePhone(index)"
              style="margin-left: 10px;"
          >
            Delete
          </el-button>
        </div>

        <!-- 添加新电话号码 -->
        <el-button type="primary" size="small" @click="addPhone">
          Add Phone
        </el-button>
      </el-form-item>
    </el-form>
  </el-dialog>




</template>

<style scoped>
.el-button {
  font-weight: bold;
}

.el-table {
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
