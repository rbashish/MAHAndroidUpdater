package com.mobapphome.mahandroidupdater;

/**
 * Created by settar on 7/12/16.
 */


import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobapphome.mahandroidupdater.tools.Constants;
import com.mobapphome.mahandroidupdater.tools.MAHUpdaterController;
import com.mobapphome.mahandroidupdater.types.DlgModeEnum;
import com.mobapphome.mahandroidupdater.types.ProgramInfo;
// ...

public class MAHRestricterDlg extends DialogFragment implements
        View.OnClickListener {

    ProgramInfo programInfo;
    DlgModeEnum type;

    boolean btnInfoVisibility;
    String btnInfoMenuItemTitle;
    String btnInfoActionURL;

    public MAHRestricterDlg() {
        // Empty constructor required for DialogFragment
    }

    public static MAHRestricterDlg newInstance(ProgramInfo programInfo,
                                               DlgModeEnum type,
                                               boolean btnInfoVisibility,
                                               String btnInfoMenuItemTitle,
                                               String btnInfoActionURL) {
        MAHRestricterDlg dialog = new MAHRestricterDlg();

        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("programInfo", gson.toJson(programInfo));
        args.putSerializable("type", type);
        args.putBoolean("btnInfoVisibility", btnInfoVisibility);
        args.putString("btnInfoMenuItemTitle", btnInfoMenuItemTitle);
        args.putString("btnInfoActionURL", btnInfoActionURL);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MAHRestricterDlg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Constants.MAH_ANDROID_UPDATER_LOG_TAG, "MAH Restricter Dlg Created ");
        Bundle arg = getArguments();
        Gson gson = new Gson();
        programInfo = gson.fromJson(arg.getString("programInfo"), ProgramInfo.class);
        type = (DlgModeEnum) arg.getSerializable("type");
        btnInfoVisibility = arg.getBoolean("btnInfoVisibility");
        btnInfoMenuItemTitle = arg.getString("btnInfoMenuItemTitle");
        btnInfoActionURL = arg.getString("btnInfoActionURL");

        Log.i(Constants.MAH_ANDROID_UPDATER_LOG_TAG, "Updateinfo from bundle " + programInfo.getUpdateInfo());

        View view = inflater.inflate(R.layout.mah_restricter_dlg, container);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.MAHUpdaterDialogAnimation;
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_BACK) {

                    onClose();
                    return true;
                }
                return false;
            }
        });

        Button btnYes = ((Button) view.findViewById(R.id.mah_updater_dlg_btn_update));
        btnYes.setOnClickListener(this);

        Button btnNo = (Button) view.findViewById(R.id.mah_updater_dlg_btn_dont_update);
        btnNo.setOnClickListener(this);

        TextView tvInfo = (TextView) view.findViewById(R.id.tvInfoTxt);

        TextView tvUpdateInfo = (TextView) view.findViewById(R.id.tvUpdateInfo);
        if (programInfo.getUpdateInfo() != null) {
            tvUpdateInfo.setText(programInfo.getUpdateInfo());
            tvUpdateInfo.setVisibility(View.VISIBLE);
        } else {
            tvUpdateInfo.setVisibility(View.GONE);
        }

        view.findViewById(R.id.mah_updater_dlg_btnCancel).setOnClickListener(this);
        ImageView ivBtnInfo = (ImageView) view.findViewById(R.id.mah_updater_dlg_btnInfo);
        ivBtnInfo.setOnClickListener(this);

        if(btnInfoVisibility){
            ivBtnInfo.setVisibility(View.VISIBLE);
        }else{
            ivBtnInfo.setVisibility(View.INVISIBLE);
        }

        switch (type) {
            case UPDATE:
                btnYes.setText(getResources().getText(R.string.cmnd_verb_mah_android_upd_dlg_btn_yes_update_txt));
                btnNo.setText(getResources().getText(R.string.cmnd_verb_mah_android_upd_dlg_btn_no_close_txt));
                tvInfo.setText(getResources().getText(R.string.mah_android_upd_restricter_info_update));
                break;

            case INSTALL:
                btnYes.setText(getResources().getText(R.string.cmnd_verb_mah_android_upd_dlg_btn_yes_install_txt));
                btnNo.setText(getResources().getText(R.string.cmnd_verb_mah_android_upd_dlg_btn_no_close_txt));
                tvInfo.setText(getResources().getText(R.string.mah_android_upd_restricter_info_install));
                break;

            case OPEN_NEW:
                btnYes.setText(getResources().getText(R.string.mah_android_upd_dlg_btn_yes_open_new_txt));
                btnNo.setText(getResources().getText(R.string.cmnd_verb_mah_android_upd_dlg_btn_no_uninstall_old_txt));
                tvInfo.setText(getResources().getText(R.string.mah_android_upd_restricter_info_open_new_version));
                tvUpdateInfo.setVisibility(View.GONE);
                break;

            case TEST:
                btnYes.setText(getResources().getText(R.string.cmnd_verb_mah_android_upd_dlg_btn_yes_update_txt));
                btnNo.setText(getResources().getText(R.string.cmnd_verb_mah_android_upd_dlg_btn_no_close_txt));
                tvInfo.setText(getResources().getText(R.string.mah_android_upd_restricter_info_update));
                break;

            default:
                break;
        }


        //Minimize the lines of question textview in  languages where question str is longer
        TextView tvQuestionTxt = (TextView) view.findViewById(R.id.tvTitle);
        String strQuest = getString(R.string.noun_mah_android_upd_dlg_title);
        if(strQuest.length() > 20){
            tvQuestionTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        }


        MAHUpdaterController.setFontTextView((TextView) view.findViewById(R.id.tvTitle));
        MAHUpdaterController.setFontTextView((TextView) view.findViewById(R.id.tvInfoTxt));
        MAHUpdaterController.setFontTextView(btnYes);
        MAHUpdaterController.setFontTextView(btnNo);

        return view;
    }

    public void onYes() {

        switch (type) {
            case OPEN_NEW:
                PackageManager pack = getActivity().getPackageManager();
                Intent app = pack.getLaunchIntentForPackage(programInfo.getUriCurrent());
                getActivity().startActivity(app);
                break;

            case INSTALL:
            case UPDATE:
                if (!programInfo.getUriCurrent().isEmpty()) {
                    Intent marketIntent = new Intent(Intent.ACTION_VIEW);
                    marketIntent.setData(Uri.parse("market://details?id=" + programInfo.getUriCurrent()));
                    try {
                        getActivity().startActivity(marketIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), getString(R.string.mah_android_upd_play_service_not_found), Toast.LENGTH_LONG).show();
                        Log.e(Constants.MAH_ANDROID_UPDATER_LOG_TAG, getString(R.string.mah_android_upd_play_service_not_found) + e.getMessage());
                    }
                }
                break;
            case TEST:
                return;
            default:
                break;
        }
    }

    public void onNo() {
        switch (type) {
            case OPEN_NEW:
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
                getActivity().startActivity(intent);

                break;

            case TEST:
            case INSTALL:
            case UPDATE:
                onClose();
                break;

            default:
                break;
        }
    }


    public void onClose() {
        dismissAllowingStateLoss();
        MAHUpdaterController.end();
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mah_updater_dlg_btnCancel) {
            onClose();
        } else if (v.getId() == R.id.mah_updater_dlg_btn_update) {
            onYes();
        } else if (v.getId() == R.id.mah_updater_dlg_btn_dont_update) {
            onNo();
        } else if (v.getId() == R.id.mah_updater_dlg_btnInfo) {
            final int itemIdForInfo = 1;
            PopupMenu popup = new PopupMenu(getContext(), v);
            popup.getMenu().add(Menu.NONE, itemIdForInfo, 1, btnInfoMenuItemTitle);

            // registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == itemIdForInfo) {
                        showMAHlib();
                    }
                    return true;
                }
            });

            popup.show();// showing popup menu
        }
    }

    private void showMAHlib() {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(btnInfoActionURL));
            getContext().startActivity(browserIntent);
        } catch (ActivityNotFoundException nfe) {
            String str = "You haven't set correct url to btnInfoActionURL, your url = " + btnInfoActionURL;
            Toast.makeText(getContext(), str, Toast.LENGTH_LONG).show();
            Log.d(Constants.MAH_ANDROID_UPDATER_LOG_TAG, str, nfe);
        }
    }
}