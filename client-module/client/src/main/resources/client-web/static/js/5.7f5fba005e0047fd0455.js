webpackJsonp([5],{"7wgv":function(s,t,e){"use strict";var a={data:function(){var s=this;return{passVisible:!1,passForm:{pass:"",checkPass:""},rulesPass:{pass:[{validator:function(t,e,a){""===e?a(new Error(s.$t("message.walletPassWord1"))):/(?!^((\d+)|([a-zA-Z]+)|([~!@#\$%\^&\*\(\)]+))$)^[a-zA-Z0-9~!@#\$%\^&\*\(\)]{8,21}$/.exec(e)?(""!==s.passForm.checkPass&&s.$refs.passForm.validateField("checkPass"),a()):a(new Error(s.$t("message.walletPassWord1")))},trigger:"blur"}],checkPass:[{validator:function(t,e,a){""===e?a(new Error(s.$t("message.affirmWalletPassWordEmpty"))):e!==s.passForm.pass?a(new Error(s.$t("message.passWordAtypism"))):a()},trigger:"blur"}]}}},created:function(){},methods:{passwordShow:function(){},passwordClose:function(){},showPasswordTwo:function(s){this.passForm.password="",this.passVisible=s},submitForm:function(s){var t=this;this.$refs[s].validate(function(s){if(!s)return!1;t.$emit("toSubmit",t.passForm.checkPass),t.passVisible=!1})},noPassword:function(){this.$emit("toSubmit",this.passForm.checkPass),this.passVisible=!1}}},o={render:function(){var s=this,t=s.$createElement,e=s._self._c||t;return e("el-dialog",{staticClass:"password-two-dialog",attrs:{title:"",visible:s.passVisible,top:"15vh"},on:{"update:visible":function(t){s.passVisible=t},open:s.passwordShow,close:s.passwordClose}},[e("h2",[s._v(s._s(s.$t("message.setPassWord")))]),s._v(" "),e("el-form",{ref:"passForm",staticClass:"set-pass",attrs:{model:s.passForm,"status-icon":"",rules:s.rulesPass}},[e("el-form-item",{staticStyle:{"margin-bottom":"5px"},attrs:{label:s.$t("message.walletPassWord"),prop:"pass"}},[e("el-input",{attrs:{type:"password",maxlength:20,placeholder:this.$t("message.walletPassWord1")},model:{value:s.passForm.pass,callback:function(t){s.$set(s.passForm,"pass",t)},expression:"passForm.pass"}})],1),s._v(" "),e("el-form-item",{staticStyle:{"margin-bottom":"5px"},attrs:{label:s.$t("message.affirmWalletPassWord"),prop:"checkPass"}},[e("el-input",{attrs:{type:"password",maxlength:20,placeholder:this.$t("message.affirmWalletPassWordEmpty")},model:{value:s.passForm.checkPass,callback:function(t){s.$set(s.passForm,"checkPass",t)},expression:"passForm.checkPass"}})],1),s._v(" "),e("div",{staticClass:"set-pass-title"},[s._v(s._s(s.$t("message.passWordInfo")))]),s._v(" "),e("el-form-item",[e("el-button",{staticClass:"set-pass-submit",attrs:{type:"primary",id:"setPassTwo"},on:{click:function(t){s.submitForm("passForm")}}},[s._v("\n                "+s._s(s.$t("message.passWordAffirm"))+"\n            ")]),s._v(" "),e("div",{staticClass:"new-no-pass",on:{click:s.noPassword}},[s._v("\n               "+s._s(s.$t("message.c159"))+"\n           ")])],1)],1)],1)},staticRenderFns:[]};var r=e("VU/8")(a,o,!1,function(s){e("kKIn")},null,null);t.a=r.exports},kKIn:function(s,t){},sz9L:function(s,t,e){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var a=e("LPk9"),o=e("7wgv"),r={data:function(){return{submitId:"importKey",keyData:{keyInfo:""},keyRules:{keyInfo:[{required:!0,message:this.$t("message.keyLow"),trigger:"blur"}]}}},components:{Back:a.a,PasswordTow:o.a},methods:{keySubmit:function(s){var t=this;this.$refs[s].validate(function(s){if(!s)return console.log("error submit!!"),!1;t.$refs.passTwo.showPasswordTwo(!0)})},toSubmit:function(s){var t=this,e="";e=""===s?'{"priKey":"'+this.keyData.keyInfo+'","password":""}':'{"priKey":"'+this.keyData.keyInfo+'","password":"'+s+'"}',this.$post("/account/import/pri",e).then(function(s){s.success?t.getAccountList("/account"):t.$message({type:"warning",message:t.$t("message.passWordFailed")+s.msg}),t.passwordVisible=!1})},getAccountList:function(s){var t=this;this.$fetch(s).then(function(s){s.success&&(t.$store.commit("setAddressList",s.data.list),1===s.data.list.length?(localStorage.setItem("newAccountAddress",s.data.list[0].address),localStorage.setItem("encrypted",s.data.list[0].encrypted),t.$router.push({name:"/wallet"})):t.$router.push({name:"/userInfo",params:{address:s.data}}),t.$message({type:"success",message:t.$t("message.passWordSuccess")}))}).catch(function(s){console.log("User List err"+s)})}}},i={render:function(){var s=this,t=s.$createElement,e=s._self._c||t;return e("div",{staticClass:"import-key"},[e("Back",{attrs:{backTitle:this.$t("message.inportAccount")}}),s._v(" "),e("h2",[s._v(s._s(s.$t("message.key")))]),s._v(" "),e("el-form",{ref:"keyData",attrs:{model:s.keyData,rules:s.keyRules,"label-position":"top"}},[e("el-form-item",{attrs:{label:s.$t("message.keyLow"),prop:"keyInfo"}},[e("el-input",{attrs:{type:"textarea",maxlength:100},model:{value:s.keyData.keyInfo,callback:function(t){s.$set(s.keyData,"keyInfo","string"==typeof t?t.trim():t)},expression:"keyData.keyInfo"}})],1),s._v(" "),e("el-form-item",[e("el-button",{attrs:{type:"primary",id:"importKey"},on:{click:function(t){s.keySubmit("keyData")}}},[s._v("\n                "+s._s(s.$t("message.confirmButtonText"))+"\n            ")])],1)],1),s._v(" "),e("PasswordTow",{ref:"passTwo",on:{toSubmit:s.toSubmit}})],1)},staticRenderFns:[]};var n=e("VU/8")(r,i,!1,function(s){e("u1TY")},null,null);t.default=n.exports},u1TY:function(s,t){}});
//# sourceMappingURL=5.7f5fba005e0047fd0455.js.map