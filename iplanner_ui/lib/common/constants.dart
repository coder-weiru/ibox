import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

// *Note*: when APP_VERSION is changed, remember to also update pubspec.yaml.
const APP_VERSION = 'v0.0.1';
const APP_NAME = 'i-Planner';
const APP_DESCRIPTION = 'An app helps manage schedule and tasks.'
    '\n\nDeveloped by Chris Ru.';

const HEADER_HEIGHT = 128.0;

final Widget splashSvg = new SvgPicture.asset(
  "assets/images/splash.svg",
  semanticsLabel: "A shark?!",
  placeholderBuilder: (BuildContext context) => new Container(
      padding: const EdgeInsets.all(30.0),
      child: const CircularProgressIndicator()),
);