package com.example.liusheng.painboard.constant;

import android.util.SparseArray;
import com.example.liusheng.painboard.R;
import com.example.liusheng.painboard.bean.LearnGroupBean;
import com.example.liusheng.painboard.bean.LearnItemBean;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenzhen on 2019/3/25.
 */

public class LearnMaterialConstant {

    static final List<LearnGroupBean> ClassifyMaterials = new ArrayList<>();

    public static List<LearnGroupBean> getClassifyMaterials() {
        if (ClassifyMaterials.size() > 0) {
            return ClassifyMaterials;
        }
        return getMaterials();
    }


    //动物
    public static final int[] DW_BG_MATERIAL = {
            R.drawable.dw_bg_material_cat,
            R.drawable.dw_bg_material_duck,
            R.drawable.dw_bg_material_dog,
            R.drawable.dw_bg_material_owl,
            R.drawable.dw_bg_material_pengguin,
            R.drawable.dw_bg_material_pig
    };

    public static final int[] DW_STEP_MATERIAL = {
            R.drawable.dw_step_material_cat,
            R.drawable.dw_step_material_duck,
            R.drawable.dw_step_material_dog,
            R.drawable.dw_step_material_owl,
            R.drawable.dw_step_material_pengguin,
            R.drawable.dw_step_material_pig
    };

    //水果
    public static final int[] SG_BG_MATERIAL = {
            R.drawable.sg_bg_material_apple,
            R.drawable.sg_bg_material_cherry,
            R.drawable.sg_bg_material_durian,
            R.drawable.sg_bg_material_mango,
            R.drawable.sg_bg_material_orange,
            R.drawable.sg_bg_material_pear,
    };
    public static final int[] SG_STEP_MATERIAL = {
            R.drawable.sg_step_material_apple,
            R.drawable.sg_step_material_cherry,
            R.drawable.sg_step_material_durian,
            R.drawable.sg_step_material_mango,
            R.drawable.sg_step_material_orange,
            R.drawable.sg_step_material_pear,
    };

    public static final List<LearnGroupBean> getMaterials() {

        //动物
        List<LearnItemBean> listDw = new ArrayList<>();
        for (int i = 0; i < DW_BG_MATERIAL.length; i++) {
            LearnItemBean bean = new LearnItemBean("动物",DW_BG_MATERIAL[i],DW_STEP_MATERIAL[i]);
            listDw.add(bean);
        }
        LearnGroupBean groupDw = new LearnGroupBean("动物",listDw);
        ClassifyMaterials.add(groupDw);

        //水果
        List<LearnItemBean> listSg = new ArrayList();
        for (int i = 0; i < SG_BG_MATERIAL.length; i++) {
            LearnItemBean bean = new LearnItemBean("水果",SG_BG_MATERIAL[i],SG_STEP_MATERIAL[i]);
            listSg.add(bean);
        }
        LearnGroupBean groupSg = new LearnGroupBean("水果",listSg);
        ClassifyMaterials.add(groupSg);
        return ClassifyMaterials;
    }
}
