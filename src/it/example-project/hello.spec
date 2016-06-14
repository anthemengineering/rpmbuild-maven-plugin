Name: hello
Version: 1.0
Release: 1%{?dist}
Summary: Example package for rpmbuild-maven-plugin

Group: Development/Libraries
License: ASL 2.0
URL: https://github.com/anthemengineering/rpmbuild-maven-plugin
Source0: http://example.com/hello-%{version}.tar.gz
Source1: http://example.com/goodbye-%{version}.txt

BuildArch: noarch

# Don't fail when generating an empty debugfiles.list
%global debug_package %{nil}

%description
Example package for rpmbuild-maven-plugin.

%prep
%setup -q


%build
mv foo bar


%install
mkdir -p %{buildroot}/usr/share/hello
cp bar %{buildroot}/usr/share/hello
touch %{buildroot}/usr/share/%{extrafilename}

%files
/usr/share/hello
/usr/share/%{extrafilename}

%changelog
* Thu Jun 09 2016 Charles Simpson - 1.0-1
- Initial commit
