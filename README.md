# Android Forms

This library is made for rendering dynamic android forms with rich attributes and many form's layouts.

### Implementation / Compile



### HOW TO USE?
```java
@formResponse JSONObject,
@mContext Activity
FormWrapper formWrapper = new FormWrapper(mContext);
formWrapper.setFormSchema(formResponse);
View formLayout = formWrapper.getFormWrapper(mContext, formResponse);
// Inject form layout in any view as per your requirement.
```

### Forms / Fields Response Format
  ```java
  // Fields JSON Format
     {"name":"title","type":"Text","required":true,"label":"Page Title"}

  // Forms JSON Format
     {"form":[{"name":"title","type":"Text","required":true,"label":"Content Title"},{"name":"limit","type":"checkbox","label":"Limit Integrations","hasSubForm":true }],"subForm":{"limit_1":[{"name":"quantity","type":"Text","label":"Quantity"}]}}
  ```
## Field wise attributes description and usages
Field | Attribute | Description
--- | --- | ---
|Common Attributes | type | Field type Ex. text, select, checkbox etc.
|| name | Name of the filed [It must be unique in the form.]
|| label | Label of the field which will be visible to user
|| value | Value of the field
|| descriptin | Descriptin of the field, to more explaine the input specific terms
|| required | To validated(Not allowed empty if true) the field at application level, it can be true or false. It is                      false by default.
|| __hasSubForm__ | If any field contains child form/field [like category >> sub-category] then make it true to render subForm. It can be true/false, by default it is false. 
|TextField| inputType | Specific attribute of TextField element which allowed type of input[number, phone, location etc]
|| hint | Hint for editbox inputs for better user experience
|Select| multiOptions | Option menus for select type field
|DateTime| spinnerType | It can be true or false, by default it is false. If ture it will render spinner view for date/time, otherwise calender view. 
|| inputType |  *Date : For date only *Time : For time only *DateTime : For date and time both
|| minDate |  To restrict minimum date
|| maxDate |  To restrict maximum date
|Checkbox|  | All common attributes are valid for checkbox element.
|SwitchElement|  | All common attributes are valid for switch element.
|FileElement| fileType | To restrict the populatuing specific files in gallery like photo, video etc. 
|Heading|  | It just uses label and very useful to categories the fields. 
|MultiSelect|  | It's layout is like grouped checkboxes which is useful when multiple choices for a single field. 
||multiOptions | Option menus for MultiSelect type field 






### Demo Screenshots Of Form Layouts/ Elements / UI


![alt text](https://raw.githubusercontent.com/kamlendrabigstep/androidTestDemo/master/screenshots/date_time_field_demo.gif)
![alt text](https://github.com/kamlendrabigstep/androidTestDemo/blob/master/screenshots/form_layouts_demo.gif)




