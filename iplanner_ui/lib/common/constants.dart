import 'package:flutter/material.dart';

// *Note*: when APP_VERSION is changed, remember to also update pubspec.yaml.
const APP_VERSION = 'v0.0.1';
const APP_NAME = 'i-Planner';
const APP_DESCRIPTION = 'An app helps manage schedule and tasks.'
    '\n\nDeveloped by Chris Ru.';

const HEADER_HEIGHT = 128.0;

final Widget SPLASH_IMAGE = Image.asset(
  'assets/images/home-office.jpg',
  fit: BoxFit.cover,
);

const MENU_EVENTS = 'My Events';
const MENU_ACTIVITIES = 'My Activities';
