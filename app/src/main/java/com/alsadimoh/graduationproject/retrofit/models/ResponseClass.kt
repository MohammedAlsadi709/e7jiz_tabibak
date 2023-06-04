package com.alsadimoh.graduationproject.retrofit.models

data class ResponseClass<T>(var status:Boolean,var message:String,var items:T,var pages:PagesModel,var errors:List<ResponseErrorsModel> )
