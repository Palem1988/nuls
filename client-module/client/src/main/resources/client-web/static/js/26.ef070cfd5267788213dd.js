webpackJsonp([26],{DzH6:function(s,e,t){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var a=t("LPk9"),o=t("KcW0"),i=t("x47x"),n={data:function(){return{showData:!1,sortValue:this.$t("message.c46"),indexTo:this.$route.query.indexTo,sortKey:"",sortConsensusList:[{sortName:this.$t("message.c46"),sortKey:""},{sortName:this.$t("message.c17"),sortKey:"commissionRate"},{sortName:this.$t("message.c25"),sortKey:"deposit"},{sortName:this.$t("message.c18"),sortKey:"creditVal"}],keyword:"",selectKeyword:"",creditValuesShow0:!1,creditValuesShow1:!1,creditValuesShow2:!1,allConsensus:[],totalAll:0,pageNumber:1}},components:{Back:a.a,ProgressBar:o.a},mounted:function(){var s="";"1"===this.indexTo?(s={pageSize:"16",pageNumber:this.pageNumber},this.indexTo="2",sessionStorage.removeItem("keyword"),sessionStorage.removeItem("sortKey"),sessionStorage.removeItem("pageNumber")):s={keyword:sessionStorage.getItem("keyword"),sortType:sessionStorage.getItem("sortKey"),pageSize:"16",pageNumber:sessionStorage.getItem("pageNumber")},this.getAllConsensus("/consensus/agent/list/",s)},methods:{getAllConsensus:function(s,e){var t=this;this.$fetch(s,e).then(function(s){if(console.log(e),console.log(s),s.success){for(var a=new i.BigNumber(1e-8),o=0;o<s.data.list.length;o++)s.data.list[o].creditVals=s.data.list[o].creditVal,s.data.list[o].deposit=parseFloat(a.times(s.data.list[o].deposit).toString()),s.data.list[o].agentAddresss=s.data.list[o].agentAddress.substr(0,6)+"..."+s.data.list[o].agentAddress.substr(-6),s.data.list[o].creditVal=((s.data.list[o].creditVal+1)/2*100).toFixed().toString()+"%",s.data.list[o].totalDeposit=parseFloat(a.times(s.data.list[o].totalDeposit).toString());t.$nextTick(function(){this.totalAll=s.data.total}),t.allConsensus=s.data.list}else t.totalAll=0,t.allConsensus=[]})},allConsensusSize:function(s){this.pageNumber=s;var e="";e=""!==this.keyword?{keyword:this.keyword,pageSize:"16",pageNumber:s}:"Comprehensive"!==this.sortValue?{sortType:this.sortKey,pageSize:"16",pageNumber:s}:""!==this.keyword&&"Comprehensive"!==this.sortValue?{keyword:this.keyword,sortType:this.sortKey,pageSize:"16",pageNumber:s}:{pageSize:"16",pageNumber:s},this.getAllConsensus("/consensus/agent/list/",e)},showDataList:function(){this.showData=!this.showData},searchConsensus:function(){if(""!==this.keyword){var s={keyword:this.keyword,pageSize:"16",pageNumber:"1"};this.getAllConsensus("/consensus/agent/list/",s)}else this.getAllConsensus("/consensus/agent/list/",{pageSize:"16",pageNumber:"1"})},sortConsensus:function(s,e){if(this.totalAll=0,this.showData=!1,this.sortValue=s,this.sortKey=e,""!==this.keyword){var t={keyword:this.keyword,sortType:e,pageSize:"16",pageNumber:"1"};this.getAllConsensus("/consensus/agent/list/",t)}else{var a={sortType:e,pageSize:"16",pageNumber:"1"};this.getAllConsensus("/consensus/agent/list/",a)}},toggleShow:function(s){this.creditValuesShow0=!this.creditValuesShow0},toNodePage:function(s){this.$router.push({path:"/consensus/nodePage",query:{address:s}}),sessionStorage.setItem("keyword",this.keyword),sessionStorage.setItem("sortKey",this.sortKey)}}},l={render:function(){var s=this,e=s.$createElement,t=s._self._c||e;return t("div",{staticClass:"agency-node"},[t("Back",{attrs:{backTitle:this.$t("message.consensusManagement")}}),s._v(" "),t("h2",[s._v(s._s(s.$t("message.c43")))]),s._v(" "),t("div",{staticClass:"agency-node-top"},[t("div",{staticClass:"search-div fl"},[t("el-input",{attrs:{placeholder:this.$t("message.c44")},model:{value:s.keyword,callback:function(e){s.keyword=e},expression:"keyword"}},[t("template",{slot:"append"},[t("el-button",{attrs:{slot:"append"},on:{click:s.searchConsensus},slot:"append"},[s._v(s._s(s.$t("message.c45")))])],1)],2)],1),s._v(" "),t("div",{staticClass:"select-div fl"},[t("div",{staticClass:"address-select sort-select",on:{click:s.showDataList}},[t("div",{staticClass:"sub-selected-value"},[s._v("\n          "+s._s(this.sortValue)+"\n          "),s.showData?t("div",{staticClass:"sub-select-list"},s._l(s.sortConsensusList,function(e){return t("div",{staticClass:"sub-select-item sort-select-item",on:{click:function(t){t.stopPropagation(),s.sortConsensus(e.sortName,e.sortKey)}}},[s._v("\n              "+s._s(e.sortName)+"\n            ")])})):s._e()]),s._v(" "),t("i",{staticClass:"el-icon-arrow-up",class:s.showData?"i_reverse":""})])])]),s._v(" "),t("div",{staticClass:"agency-node-bottom"},s._l(s.allConsensus,function(e,a){return t("div",{staticClass:"div-icon cursor-p",on:{click:function(t){s.toNodePage(e.agentHash)}}},[t("p",{staticClass:"subscript",class:0===e.status?"stay":""},[s._v("\n        "+s._s(s.$t("message.status"+e.status))+"\n      ")]),s._v(" "),t("h3",[s._v(s._s(e.agentId))]),s._v(" "),t("ul",[t("li",{staticClass:"overflow"},[t("label",[s._v(s._s(s.$t("message.c16"))+"：")]),s._v(s._s(e.agentName?e.agentName:e.agentAddresss)+"\n        ")]),s._v(" "),t("li",[t("label",[s._v(s._s(s.$t("message.c17"))+"：")]),s._v(s._s(e.commissionRate)+"%")]),s._v(" "),t("li",[t("label",[s._v(s._s(s.$t("message.c25"))+"：")]),s._v(s._s(e.deposit.toFixed(2))+" NULS")]),s._v(" "),t("li",{staticClass:"cb"},[t("label",{staticClass:"fl"},[s._v(s._s(s.$t("message.c47"))+"：")]),s._v(s._s(e.totalDeposit.toFixed(2))+"\n        ")]),s._v(" "),t("li",{on:{mouseover:function(e){s.toggleShow(a)},mouseout:function(e){s.toggleShow(a)}}},[t("label",{staticClass:"fl cursor-p"},[s._v(s._s(s.$t("message.c18"))+":")]),s._v(" "),t("ProgressBar",{attrs:{colorData:e.creditVals<0?"#f64b3e":"#82bd39",widthData:e.creditVal}})],1)])])})),s._v(" "),t("el-pagination",{directives:[{name:"show",rawName:"v-show",value:s.totalOK=this.totalAll>16,expression:"totalOK = this.totalAll > 16 ? true:false"}],staticClass:"cb",attrs:{layout:"prev, pager, next","page-size":16,total:this.totalAll},on:{"current-change":s.allConsensusSize}})],1)},staticRenderFns:[]};var r=t("vSla")(n,l,!1,function(s){t("Y4cW")},null,null);e.default=r.exports},Y4cW:function(s,e){}});