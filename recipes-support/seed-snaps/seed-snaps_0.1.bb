DESCRIPTION = "Seed initial snap packages"
HOMEPAGE = "https://github.com/crossbario/meta-snappy"
BUGTRACKER = "https://github.com/crossbario/meta-snappy"

LICENSE = "MIT"

do_install() {
        install -d ${D}/var/lib/snapd/seed
        tar xf ${DL_DIR}/${PN}_${TARGET_ARCH}_${PV}.tar.gz -C ${D}
        chown -R root:root ${D}
}


do_fetch() {
        if [ ! -f 'snap-exe_${PV}' ]; then
                wget https://s3.eu-central-1.amazonaws.com/download.crossbario.com/crossbarfx/snap-exe -O snap-exe_${PV}
                chmod +x snap-exe_${PV}
        fi

        if [ "${TARGET_ARCH}" = "aarch64" ]; then
                ARCH=arm64
        elif [ "${TARGET_ARCH}" = "x86_64" ]; then
                ARCH=amd64
        else
                echo Unsupported TARGET ARCH ${TARGET_ARCH}
                exit 1
        fi

        if [ ! -f '${PN}_${TARGET_ARCH}_${PV}.tar.gz' ]; then
                ./snap-exe_${PV} known --remote model series=16 brand-id=generic model=generic-classic > ./classic.model
                ./snap-exe_${PV} prepare-image --classic --arch=$ARCH classic.model . --snap core --snap core20 --snap crossbarfx=edge
                tar -czvf ${PN}_${TARGET_ARCH}_${PV}.tar.gz var
        fi
}


do_cleanall() {
        rm -f ${DL_DIR}/snap-exe_${PV}
        rm -f ${DL_DIR}/${PN}_${TARGET_ARCH}_${PV}.tar.gz
}
