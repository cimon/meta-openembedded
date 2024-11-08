SUMMARY = "Distributed block device driver for Linux"
DESCRIPTION = "DRBD mirrors a block device over the network to another machine.\
DRBD mirrors a block device over the network to another machine.\
Think of it as networked raid 1. It is a building block for\
setting up high availability (HA) clusters."
HOMEPAGE = "http://www.drbd.org/"
SECTION = "admin"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=5574c6965ae5f583e55880e397fbb018"

SRC_URI = "git://github.com/LINBIT/drbd-utils;name=drbd-utils;branch=master;protocol=https \
           git://github.com/LINBIT/drbd-headers;name=drbd-headers;destsuffix=git/drbd-headers \
           ${@bb.utils.contains('DISTRO_FEATURES','usrmerge','file://0001-drbd-utils-support-usrmerge.patch','',d)} \
          "
# v9.10.0
SRCREV_drbd-utils = "859151b228d3b3aacefb09d06d515a2589c22e35"
SRCREV_drbd-headers = "0955b3423f08f8e11ff05092bc1b766609fd804b"

S = "${WORKDIR}/git"

UPSTREAM_CHECK_URI = "https://github.com/LINBIT/drbd-utils/releases"

SYSTEMD_SERVICE_${PN} = "drbd.service"
SYSTEMD_AUTO_ENABLE = "disable"

inherit autotools-brokensep systemd

EXTRA_OECONF = " \
                --with-initdir=/etc/init.d    \
                --without-pacemaker           \
                --without-rgmanager           \
                --without-bashcompletion      \
                --with-distro debian          \
                --with-initscripttype=both    \
                --with-systemdunitdir=${systemd_unitdir}/system \
                --without-manual \
               "

# If we have inherited reproducible_build, we want to use it.
export WANT_DRBD_REPRODUCIBLE_BUILD = "yes"

do_install_append() {
    # don't install empty /var/lock and /var/run to avoid conflict with base-files
    rm -rf ${D}${localstatedir}/lock
    rm -rf ${D}${localstatedir}/run
}

RDEPENDS_${PN} += "bash perl-module-getopt-long perl-module-exporter perl-module-constant perl-module-overloading perl-module-exporter-heavy"

# The drbd items are explicitly put under /lib when installed.
#
FILES_${PN} += "/run"
FILES_${PN} += "${nonarch_base_libdir}/drbd \
                ${nonarch_libdir}/drbd \
                ${nonarch_libdir}/tmpfiles.d"
FILES_${PN}-dbg += "${nonarch_base_libdir}/drbd/.debug"
