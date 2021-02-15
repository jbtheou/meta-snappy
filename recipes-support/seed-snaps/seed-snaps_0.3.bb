FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

DESCRIPTION = "Seed initial snap packages"
HOMEPAGE = "https://github.com/crossbario/meta-snappy"
BUGTRACKER = "https://github.com/crossbario/meta-snappy"

LICENSE = "MIT"

SRC_URI += "\
	https://s3.eu-central-1.amazonaws.com/download.crossbario.com/crossbarfx/snap-exe \
	file://COPYING \
"
SRC_URI[sha256sum] = "4a65e204e40409519ac896536a07072f5ff4e6289c30d5bf185159d6608fba0a"
LIC_FILES_CHKSUM = "file://${WORKDIR}/COPYING;md5=f597f124e3f6cde279112fccc78ddba9"


do_install() {
	chmod +x ${WORKDIR}/snap-exe
	${WORKDIR}/snap-exe known --remote model series=16 brand-id=generic model=generic-classic > ./classic.model
	SNAPS_TO_INSTALL=''
	for snap in ${SNAPPY_PREINSTALLED}; do SNAPS_TO_INSTALL="${SNAPS_TO_INSTALL} --snap ${snap} "; done
        if [ "${TARGET_ARCH}" = "aarch64" ]; then
                ARCH=arm64
        elif [ "${TARGET_ARCH}" = "x86_64" ]; then
                ARCH=amd64
        else
                echo Unsupported TARGET ARCH ${TARGET_ARCH}
                exit 1
        fi

	# Clean any existing downloads
	rm -rf ./var
	${WORKDIR}/snap-exe prepare-image --classic --arch=${ARCH} classic.model . ${SNAPS_TO_INSTALL}
	cp -a var ${D}
	chown -R root:root ${D}
}
