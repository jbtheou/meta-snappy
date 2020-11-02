DESCRIPTION = "Seed initial snap packages"
HOMEPAGE = "https://github.com/crossbario/meta-snappy"
BUGTRACKER = "https://github.com/crossbario/meta-snappy"

LICENSE = "MIT"

do_install() {
        install -d ${D}/var/lib/snapd/seed
        tar xf ${DL_DIR}/seed-snaps_0.1.tar.gz -C ${D}
        chown -R root:root ${D}
}


do_fetch() {
        wget https://s3.eu-central-1.amazonaws.com/download.crossbario.com/crossbarfx/snap-exe
        chmod +x snap-exe
        ./snap-exe known --remote model series=16 brand-id=generic model=generic-classic > ./classic.model
        ./snap-exe prepare-image --classic --arch=arm64 classic.model . --snap core --snap core20 --snap crossbar
        tar -czvf seed-snaps_0.1.tar.gz var
        touch seed-snaps_0.1.tar.gz.done
        chmod 664 seed-snaps_0.1.tar.gz.done
        rm -r var classic.model
}
