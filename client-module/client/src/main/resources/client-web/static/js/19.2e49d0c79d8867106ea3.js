webpackJsonp([19],{"26tB":function(e,t){},gjI2:function(e,t,s){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var a=s("LPk9"),o=s("KcW0"),n=s("+1pJ"),i=s("FJop"),r=(s("QmSG"),s("x47x")),d=s("YgNb"),c={data:function(){var e=this;return{loading:!0,btOk:this.$store.getters.getNetWorkInfo.localBestHeight!==this.$store.getters.getNetWorkInfo.netBestHeight,submitId:"nodePage",address:this.$route.query.address,agentId:"",nodeData:[],usable:0,fee:0,maxAmount:0,utxoTip:"",nodeForm:{nodeNo:""},nodeRules:{nodeNo:[{validator:function(t,s,a){s||a(new Error(e.$t("message.c52"))),setTimeout(function(){var t=new r.BigNumber(1e8),o=new r.BigNumber(1e-8);if(t.times(e.nodeForm.nodeNo).toString()===t.times(e.usable).toString()&&(e.nodeForm.nodeNo=o.times(t.times(e.usable)-t.times(e.fee)).toString(),s=e.nodeForm.nodeNo),/^\d+(?=\.{0,1}\d+$|$)/.exec(s)&&/^\d{1,8}(\.\d{1,8})?$/.exec(s)){var n=new r.BigNumber(s),i=new r.BigNumber(e.usable);if(s<2e3)a(new Error(e.$t("message.c54")));else if(1===n.comparedTo(i.minus(e.fee)))a(new Error(e.$t("message.c542")));else if(Object(d.e)(s)>e.maxAmount){var c=e.utxoTip?e.utxoTip:e.$t("message.c202")+Object(d.b)(e.maxAmount);a(new Error(c))}else a()}else a(new Error(e.$t("message.c53")))},100)},trigger:"blur"}]},toCheckOk:!1}},components:{Back:a.a,ProgressBar:o.a,AccountAddressBar:n.a,Password:i.a},created:function(){this.getConsensusAddress("/consensus/agent/"+this.address),this.getBalanceAddress("/accountledger/balance/"+localStorage.getItem("newAccountAddress"))},mounted:function(){this.$refs.input.focus(),this.$refs.input.value="",sessionStorage.setItem("passwordOk","0")},beforeRouteLeave:function(e,t,s){"/consensus"===e.name||(sessionStorage.removeItem("consensusTotalAll"),sessionStorage.removeItem("consensusAllEvents")),s()},methods:{getConsensusAddress:function(e){var t=this;this.$fetch(e).then(function(e){if(e.success){var s=new r.BigNumber(1e-8);t.toCheckOk=e.data.agentAddress===localStorage.getItem("newAccountAddress"),e.data.deposit=parseFloat(s.times(e.data.deposit).toString()),e.data.creditVals=e.data.creditVal,e.data.creditVal=((e.data.creditVal+1)/2*100).toFixed().toString()+"%",e.data.agentAddresss=e.data.agentAddress.substr(0,10)+"..."+e.data.agentAddress.substr(-10),e.data.totalDeposits=(1e-8*e.data.totalDeposit).toFixed(0)+"/500000",e.data.totalDeposit>5e13?e.data.totalDeposit="100%":e.data.totalDeposit=(e.data.totalDeposit/5e11).toString()+"%",t.agentId=e.data.agentHash,t.nodeData=e.data,t.loading=!1}})},chenckAccountAddress:function(e){this.getBalanceAddress("/accountledger/balance/"+e),this.$refs.nodeForm.validateField("nodeNo")},getBalanceAddress:function(e){var t=this;this.$fetch(e).then(function(e){if(e.success){var s=new r.BigNumber(1e-8);t.usable=parseFloat(s.times(e.data.usable.value).toString())}})},toCheck:function(){this.$router.push({path:"/consensus/nodeInfo",query:{agentHash:this.agentId}})},zeroToWhole:function(){this.$router.push({name:"zeroToWhole"})},countFee:function(){var e=this;if(this.nodeForm.nodeNo>0){var t=new r.BigNumber(1e8),s="address="+localStorage.getItem("newAccountAddress")+"&agentHash="+this.agentId+"&deposit="+t.times(this.nodeForm.nodeNo);this.$fetch("/consensus/deposit/fee?"+s).then(function(t){t.success?(e.fee=Object(d.b)(t.data.fee),t.data.maxAmount?e.maxAmount=t.data.maxAmount:e.maxAmount=1e19):(e.utxoTip=e.$t("message.c288"),e.$message({message:e.$t("message.passWordFailed")+": "+t.data.msg,type:"warning",showClose:!0}))})}},onSubmit:function(e){var t=this;this.$store.getters.getNetWorkInfo.localBestHeight===this.$store.getters.getNetWorkInfo.netBestHeight&&"true"===sessionStorage.getItem("setNodeNumberOk")?this.$refs[e].validate(function(e){if(!e)return!1;"true"===localStorage.getItem("encrypted")?t.$refs.password.showPassword(!0):t.$confirm(t.$t("message.c165"),"",{confirmButtonText:t.$t("message.confirmButtonText"),cancelButtonText:t.$t("message.cancelButtonText")}).then(function(){t.toSubmit("")}).catch(function(){})}):this.$message({message:this.$t("message.c133"),duration:"800"})},toSubmit:function(e){var t=this,s=new r.BigNumber(1e8),a='{"address":"'+localStorage.getItem("newAccountAddress")+'","agentHash":"'+this.agentId+'","deposit":"'+parseFloat(s.times(this.nodeForm.nodeNo).toString())+'","password":"'+e+'"}';this.$post("/consensus/deposit/",a).then(function(e){e.success?(t.$message({message:t.$t("message.passWordSuccess"),type:"success"}),t.$router.push({name:"/consensus",params:{activeName:"first"}})):t.$message({message:t.$t("message.passWordFailed")+e.data.msg,type:"warning"})})}}},l={render:function(){var e=this,t=e.$createElement,s=e._self._c||t;return s("div",{staticClass:"node-page"},[s("Back",{attrs:{backTitle:this.$t("message.consensusManagement")}}),e._v(" "),s("h2",[e._v(e._s(this.nodeData.agentId))]),e._v(" "),s("div",{directives:[{name:"loading",rawName:"v-loading",value:e.loading,expression:"loading"}],staticClass:"div-icon1 node-page-top"},[s("p",{staticClass:"subscript",class:0===this.nodeData.status?"stay":""},[e._v("\n      "+e._s(e.$t("message.status"+this.nodeData.status))+"\n    ")]),e._v(" "),s("ul",[s("li",{staticClass:"li-bg overflow"},[s("label",[e._v(e._s(e.$t("message.c16"))+"：")]),e._v(e._s(this.nodeData.agentName?this.nodeData.agentName:this.nodeData.agentAddresss)+"\n        "),s("span",{directives:[{name:"show",rawName:"v-show",value:e.toCheckOk,expression:"toCheckOk"}],staticClass:"cursor-p text-d",on:{click:function(t){e.toCheck()}}},[e._v(e._s(e.$t("message.c5_1")))])]),e._v(" "),s("li",[s("label",[e._v(e._s(e.$t("message.c17"))+"：")]),e._v(e._s(this.nodeData.commissionRate)+"%\n      ")]),e._v(" "),s("li",[s("label",[e._v(e._s(e.$t("message.c25"))+"：")]),e._v(e._s(this.nodeData.deposit)+" NULS\n      ")]),e._v(" "),s("li",[s("label",[e._v(e._s(e.$t("message.c19"))+"：")]),e._v(e._s(this.nodeData.memberCount)+"\n      ")]),e._v(" "),s("li",[s("label",[e._v(e._s(e.$t("message.c18"))+"：")]),e._v(" "),s("ProgressBar",{attrs:{colorData:this.nodeData.creditVals<0?"#f64b3e":"#82bd39",widthData:this.nodeData.creditVal}}),e._v(" "),s("span",[e._v(e._s(this.nodeData.creditVals))])],1),e._v(" "),s("li",[s("label",[e._v(e._s(e.$t("message.c64"))+"：")]),e._v(" "),s("ProgressBar",{attrs:{colorData:"#58a5c9",widthData:this.nodeData.totalDeposit}}),e._v(" "),s("span",[e._v(e._s(this.nodeData.totalDeposits))])],1)])]),e._v(" "),s("div",{staticClass:"node-page-bottom"},[s("el-form",{ref:"nodeForm",attrs:{model:e.nodeForm,rules:e.nodeRules,size:"mini","label-position":"left"},nativeOn:{submit:function(e){e.preventDefault()}}},[s("el-form-item",{staticClass:"account-address",attrs:{label:e.$t("message.newAccountAddress")+":"}},[s("AccountAddressBar",{on:{chenckAccountAddress:e.chenckAccountAddress}})],1),e._v(" "),s("span",{staticClass:"allUsable"},[e._v(e._s(e.$t("message.currentBalance"))+": "+e._s(this.usable.toFixed(8))+" NULS")]),e._v(" "),s("el-form-item",{staticClass:"number",attrs:{label:e.$t("message.c25")+":",prop:"nodeNo"}},[s("el-input",{ref:"input",attrs:{maxlength:17},on:{change:e.countFee},model:{value:e.nodeForm.nodeNo,callback:function(t){e.$set(e.nodeForm,"nodeNo","string"==typeof t?t.trim():t)},expression:"nodeForm.nodeNo"}})],1),e._v(" "),s("div",{staticClass:"procedure"},[s("label",[e._v(e._s(e.$t("message.c28"))+":")]),e._v(" "),s("span",[e._v(e._s(this.fee.toString())+" NULS")]),e._v(" "),s("h5",{staticClass:"zeroToWhole"},[s("span",{staticClass:"cursor-p text-ds",on:{click:e.zeroToWhole}},[e._v(e._s(e.$t("message.type51")))])])]),e._v(" "),s("el-form-item",{staticClass:"submit",attrs:{size:"large"}},[s("el-button",{attrs:{type:"primary",id:"nodePage"},on:{click:function(t){e.onSubmit("nodeForm")}}},[e._v("\n          "+e._s(e.$t("message.confirmButtonText"))+"\n        ")])],1)],1)],1),e._v(" "),s("Password",{ref:"password",attrs:{submitId:e.submitId},on:{toSubmit:e.toSubmit}})],1)},staticRenderFns:[]};var u=s("vSla")(c,l,!1,function(e){s("26tB")},null,null);t.default=u.exports}});