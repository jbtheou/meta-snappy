FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SUMMARY = "The snapd and snap tools enable systems to work with .snap files."
HOMEPAGE = "https://www.snapcraft.io"
LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/snapd-${PV}/COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI = "									\
	https://${GO_IMPORT}/releases/download/${PV}/snapd_${PV}.vendor.tar.xz	\
	file://0001-Remove-UDEV-check.patch \
	file://0002-trim-code-for-Yocto-usecase.patch \
"

SRC_URI[md5sum] = "448e3d2d2cefff1156d946e2bdcf65bc"
SRC_URI[sha256sum] = "c1a0c19420594dee222779fd2c8472a65173291b445727a6bd1bbc83f79d1687"

GO_IMPORT = "github.com/snapcore/snapd"

SHARED_GO_INSTALL = "				\
	${GO_IMPORT}/cmd/snap		\
	"

GO_INSTALL = "${SHARED_GO_INSTALL}"

DEPENDS += "		\
	glib-2.0-native	\
"

RDEPENDS_${PN} += "		\
	ca-certificates		\
	bash \
"

S = "${WORKDIR}/snapd-${PV}"

EXTRA_OECONF += "			\
	--disable-apparmor		\
	--libexecdir=${libdir}/snapd	\
	--with-snap-mount-dir=/snap \
"

inherit autotools pkgconfig go native

# disable shared runtime for x86
# https://forum.snapcraft.io/t/yocto-rocko-core-snap-panic/3261
# GO_DYNLINK is set with arch overrides in goarch.bbclass
GO_DYNLINK_x86 = ""
GO_DYNLINK_x86-64 = ""
GO_DYNLINK_arm = ""
GO_DYNLINK_aarch64 = ""

# Our tools build with autotools are inside the cmd subdirectory
# and we need to tell the autotools class to look in there.
AUTOTOOLS_SCRIPT_PATH = "${S}/cmd"
do_configure_prepend() {
	(cd ${S} ; ./mkversion.sh ${PV})
}

# The go class does export a do_configure function, of which we need
# to change the symlink set-up, to target snapd's environment.
do_configure() {
	mkdir -p ${S}/src/github.com/snapcore
	ln -snf ${S} ${S}/src/${GO_IMPORT}
	go_do_configure
	autotools_do_configure
}

do_compile() {
	go_do_compile
	# build the rest
    autotools_do_compile
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/${GO_BUILD_BINDIR}/snap ${D}${bindir}
}

RDEPENDS_${PN} += "squashfs-tools"

INHIBIT_SYSROOT_STRIP = "1"
