import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class MyAppSettings extends ChangeNotifier {
  static const _darkModePreferenceKey = 'DARK_MODE';

  final SharedPreferences _pref;

  MyAppSettings(this._pref);

  bool get isDarkMode => _pref?.getBool(_darkModePreferenceKey) ?? false;

  void setDarkMode(bool val) {
    _pref?.setBool(_darkModePreferenceKey, val);
    notifyListeners();
  }
}
