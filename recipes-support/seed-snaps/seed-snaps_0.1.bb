DESCRIPTION = "Seed initial snap packages"
HOMEPAGE = "https://github.com/crossbario/meta-snappy"
BUGTRACKER = "https://github.com/crossbario/meta-snappy"

LICENSE = "MIT"

download_seeds() {
        if [ ! -f 'snap-exe' ]; then
                wget https://s3.eu-central-1.amazonaws.com/download.crossbario.com/crossbarfx/snap-exe
                chmod +x snap-exe
        fi

        if [ "${TARGET_ARCH}" = "aarch64" ]; then
                ARCH=arm64
        elif [ "${TARGET_ARCH}" = "x86_64" ]; then
                ARCH=amd64
        else
                echo Unsupported TARGET ARCH ${TARGET_ARCH}
                exit 1
        fi

        if [ ! -f 'seed-snaps_${TARGET_ARCH}0.1.tar.gz' ]; then
                ./snap-exe known --remote model series=16 brand-id=generic model=generic-classic > ./classic.model
                ./snap-exe prepare-image --classic --arch=$ARCH classic.model . --snap core --snap core20 --snap crossbar
                tar -czvf seed-snaps_${TARGET_ARCH}_0.1.tar.gz var
        fi
}

do_install() {
        install -d ${D}/var/lib/snapd/seed
        tar xf ${DL_DIR}/seed-snaps_${TARGET_ARCH}_0.1.tar.gz -C ${D}
        chown -R root:root ${D}
}


do_fetch() {
        download_seeds
}
