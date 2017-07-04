INSERT INTO sm_funcregister (ts, funtype, isbuttonpower, fun_name, funcode, isenable, iscauserusable, fun_desc, class_name, fun_property, isfunctype, orgtypecode, cfunid, pk_group, help_name, mdid, parent_id, dr, own_module ) VALUES ('2017-06-22 14:29:41', 0, 'N', '奖金分配单元', '60135007', 'Y', null, null, 'nc.ui.pubapp.uif2app.ToftPanelAdaptorEx', 0, 'N', 'GROUPORGTYPE00000000', '0001ZZ10000000001XG1', '~', null, '~', '1001ZZ10000000005YIA', 0, '6013' );
INSERT INTO sm_paramregister (ts, paramname, pk_param, parentid, paramvalue, dr ) VALUES ('2017-06-22 14:29:41', 'BeanConfigFilePath', '0001ZZ10000000001XG2', '0001ZZ10000000001XG1', 'nc/ui/hrwa/wa_ba_unit/ace/view/WaBaUnit_config.xml', 0 );
INSERT INTO sm_menuitemreg (ts, iconpath, menuitemname, pk_menu, resid, ismenutype, pk_menuitem, menudes, funcode, menuitemcode, dr ) VALUES ('2017-06-22 14:29:41', null, '奖金分配单元', '1004ZZ10000000000FFL', 'D60135007', 'N', '0001ZZ10000000001XG3', null, '60135007', '60135007', 0 );
INSERT INTO pub_billtemplet (ts, BILL_TEMPLETCAPTION, BILL_TEMPLETNAME, NODECODE, PK_BILLTEMPLET, PK_BILLTYPECODE, PK_CORP, METADATACLASS, MODULECODE, LAYER ) VALUES ('2017-06-22 14:29:49', '奖金分配单元', 'SYSTEM', '60135007', '0001ZZ10000000001XG4', '60135007', '@@@@', 'hrwa.WaBaUnitHVO', '6013', 0 );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'pk_wa_ba_unit', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XG5', 0, 'N', 'N', 0, 1, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.pk_wa_ba_unit', 'pk_wa_ba_unit', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'code', 'N', 1, 'Y', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XG6', 0, 'N', 'N', 1, 2, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.code', 'code', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'name', 'N', 1, 'Y', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XG7', 0, 'N', 'N', 1, 3, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.name', 'name', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'ba_unit_type', 'N', 1, 'Y', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XG8', 0, 'N', 'N', 1, 4, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.ba_unit_type', 'ba_unit_type', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'ba_mng_psnpk', 'N', 1, 'Y', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XG9', 0, 'N', 'N', 1, 5, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.ba_mng_psnpk', 'ba_mng_psnpk', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'ba_mng_psnpk_showname', 'N', 1, 'Y', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGA', 0, 'N', 'N', 1, 6, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.ba_mng_psnpk_showname', 'ba_mng_psnpk_showname', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'src_obj_pk', 'N', 1, 'Y', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGB', 0, 'N', 'N', 1, 7, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.src_obj_pk', 'src_obj_pk', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'pk_group', 'N', 1, 'Y', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGC', 0, 'N', 'N', 1, 8, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.pk_group', 'pk_group', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'pk_org', 'N', 1, 'Y', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGD', 0, 'N', 'N', 1, 9, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.pk_org', 'pk_org', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'pk_org_v', 'N', 1, 'Y', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGE', 0, 'N', 'N', 1, 10, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.pk_org_v', 'pk_org_v', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'creator', 'N', 1, 'Y', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGF', 0, 'N', 'N', 1, 11, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.creator', 'creator', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'creationtime', 'N', 1, 'Y', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGG', 0, 'N', 'N', 1, 12, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.creationtime', 'creationtime', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'modifier', 'N', 1, 'Y', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGH', 0, 'N', 'N', 1, 13, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.modifier', 'modifier', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'modifiedtime', 'N', 1, 'Y', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGI', 0, 'N', 'N', 1, 14, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.modifiedtime', 'modifiedtime', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef1', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGJ', 0, 'N', 'N', 0, 15, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef1', 'vdef1', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef2', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGK', 0, 'N', 'N', 0, 16, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef2', 'vdef2', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef3', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGL', 0, 'N', 'N', 0, 17, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef3', 'vdef3', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef4', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGM', 0, 'N', 'N', 0, 18, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef4', 'vdef4', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef5', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGN', 0, 'N', 'N', 0, 19, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef5', 'vdef5', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef6', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGO', 0, 'N', 'N', 0, 20, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef6', 'vdef6', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef7', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGP', 0, 'N', 'N', 0, 21, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef7', 'vdef7', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef8', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGQ', 0, 'N', 'N', 0, 22, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef8', 'vdef8', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef9', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGR', 0, 'N', 'N', 0, 23, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef9', 'vdef9', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef10', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGS', 0, 'N', 'N', 0, 24, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef10', 'vdef10', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef11', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGT', 0, 'N', 'N', 0, 25, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef11', 'vdef11', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef12', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGU', 0, 'N', 'N', 0, 26, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef12', 'vdef12', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef13', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGV', 0, 'N', 'N', 0, 27, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef13', 'vdef13', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef14', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGW', 0, 'N', 'N', 0, 28, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef14', 'vdef14', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef15', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGX', 0, 'N', 'N', 0, 29, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef15', 'vdef15', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef16', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGY', 0, 'N', 'N', 0, 30, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef16', 'vdef16', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef17', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XGZ', 0, 'N', 'N', 0, 31, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef17', 'vdef17', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef18', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XH0', 0, 'N', 'N', 0, 32, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef18', 'vdef18', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef19', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XH1', 0, 'N', 'N', 0, 33, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef19', 'vdef19', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'vdef20', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XH2', 0, 'N', 'N', 0, 34, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.vdef20', 'vdef20', 'N', 'N' );
INSERT INTO pub_billtemplet_b (ts, CARDFLAG, DATATYPE, EDITFLAG, FOREGROUND, INPUTLENGTH, ITEMKEY, LEAFFLAG, LISTFLAG, LISTSHOWFLAG, LOCKFLAG, NEWLINEFLAG, NULLFLAG, PK_BILLTEMPLET, PK_BILLTEMPLET_B, POS, REVISEFLAG, USERREVISEFLAG, SHOWFLAG, SHOWORDER, TABLE_CODE, TABLE_NAME, TOTALFLAG, USERDEFFLAG, USEREDITFLAG, USERFLAG, USERSHOWFLAG, WIDTH, METADATAPROPERTY, METADATAPATH, HYPERLINKFLAG, LISTHYPERLINKFLAG ) VALUES ('2017-06-22 14:29:49', 1, -1, 1, -1, -1, 'ts', 'N', 1, 'N', 0, 'N', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XH3', 0, 'N', 'N', 0, 35, 'WaBaUnitHVO', '奖金分配单元', 0, 'N', 1, 1, 1, 1, 'hrwa.WaBaUnitHVO.ts', 'ts', 'N', 'N' );
INSERT INTO pub_billtemplet_t (ts, position, resid, tabindex, vdef2, vdef1, pk_billtemplet_t, vdef3, metadatapath, tabcode, pos, pk_billtemplet, metadataclass, basetab, mixindex, tabname, pk_layout ) VALUES ('2017-06-22 14:29:49', null, null, 0, null, null, '0001ZZ10000000001XH4', null, 'WaBaUnitHVO', 'WaBaUnitHVO', 0, '0001ZZ10000000001XG4', 'hrwa.WaBaUnitHVO', null, null, '奖金分配单元', '~' );
INSERT INTO aam_appasset (ts, dataidname, isvalid, creator, pk_developorg, dataidname2, dataidname3, dataidname4, dataidname5, pk_module, pk_org, dataidname6, pk_aamindustry, assetcode, dataid, dr, def5, pk_industry, modifier, creationtime, pk_countryzone, pk_assettype, assetdesc, assetname2, pk_asset, assetname4, assetname3, modifiedtime, assetname6, pk_group, assetname5, pk_developer, def4, assetlayer, def3, def2, def1, assetname ) VALUES ('2017-06-22 14:29:51', 'SYSTEM', '1', '#UAP#', '00001', null, null, null, null, '6013', 'GLOBLE00000000000000', null, null, 'AANCAAM170600000028', '0001ZZ10000000001XG4', 0, null, '~', '~', '2017-06-22 14:29:51', '~', '1001Z01000000000MWUT', 'billtemplet', null, '0001ZZ10000000001XH5', null, null, null, null, '~', null, '1004Z11000000001125X', null, '0', null, null, null, '单据模板-SYSTEM' );
INSERT INTO pub_systemplate_base (ts, nodekey, layer, templateid, pk_systemplate, pk_industry, tempstyle, pk_country, funnode, moduleid, devorg, dr ) VALUES ('2017-06-22 14:29:52', 'bt', 0, '0001ZZ10000000001XG4', '0001ZZ10000000001XH6', '~', 0, '~', '60135007', '6013', '00001', 0 );