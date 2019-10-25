import 'package:flutter/material.dart';

@immutable
class MyTab {
  final Widget child;
  final String tabName;
  final int tabIndex;
  final Icon _tabNavIcon;
  final String _tabDisplayText;

  const MyTab(
      {@required this.child,
      @required this.tabName,
      @required this.tabIndex,
      Icon navIcon,
      String displayText})
      : _tabNavIcon = navIcon,
        _tabDisplayText = displayText;

  String get tabDisplayText => _tabDisplayText ?? this.tabName;

  Icon get tabNavIcon => _tabNavIcon;
}

class TabbedPage extends StatefulWidget {
  // A set of Tabs in the tabbed page.
  final List<MyTab> _tabs;
  // Title shown in the route's appbar. By default just returns routeName.
  final String _title;
  // A short description of the route. If not null, will be shown as subtitle in
  final String description;
  // The name of the route.
  final String _routeName;

  const TabbedPage({
    Key key,
    @required tabs,
    int initialTabIndex,
    String title,
    this.description,
    @required routeName,
  })  : _tabs = tabs,
        _title = title,
        _routeName = routeName,
        super(key: key);

  String get routeName => this._routeName;

  String get title => _title ?? this.routeName;

  @override
  State<StatefulWidget> createState() => _TabbedPageState();
}

class _TabbedPageState extends State<TabbedPage> {
  int _currentTabIndex = 0;

  void changeTab(int index) {
    setState(() {
      _currentTabIndex = index;
    });
  }

  @override
  Widget build(BuildContext context) {
    final _tabPages =
        widget._tabs.map((MyTab tab) => Center(child: tab.child)).toList();
    final _bottomNavBarItems = widget._tabs
        .map((MyTab tab) => BottomNavigationBarItem(
            icon: tab._tabNavIcon, title: Text(tab._tabDisplayText)))
        .toList();

    assert(_tabPages.length == _bottomNavBarItems.length);

    final bottomNavBar = BottomNavigationBar(
      items: _bottomNavBarItems,
      currentIndex: _currentTabIndex,
      type: BottomNavigationBarType.fixed,
      onTap: (int index) {
        changeTab(index);
      },
    );
    return Scaffold(
      body: _tabPages[_currentTabIndex],
      bottomNavigationBar: bottomNavBar,
    );
  }
}
