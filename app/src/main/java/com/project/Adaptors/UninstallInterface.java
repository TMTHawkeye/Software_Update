package com.project.Adaptors;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.project.Helper.AppsModel;

import java.util.ArrayList;

public interface UninstallInterface
{
 void deleteApp(ArrayList<AppsModel> list,int position);
}