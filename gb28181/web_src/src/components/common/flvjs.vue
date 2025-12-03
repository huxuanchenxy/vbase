<template>
    <video ref="container" id="flvjs-video" disablePictureInPicture></video>
</template>
<script>
    import flvjs from 'flv.js'

    export default {
        name: 'flvjs',
        data() {
            return {
                player: null,
            };
        },
        props: ['videoUrl'],
        mounted() {
            this.$nextTick(() => {
                this.updatePlayerDomSize();
                
                window.onresize = () => {
                    this.updatePlayerDomSize()
                };
            });

            this.init(null);
        },
        watch: {
            videoUrl(newData, oldData) {
                if (!!newData) {
                    this.play(newData);
                }
            },
            immediate: true
        },
        destroyed() {
            this.stop();
            this.player = null;
        },
        methods: {
            updatePlayerDomSize() {
                let dom = this.$refs.container;
                let width = dom.parentNode.clientWidth
                let height = (9 / 16) * width

                const clientHeight = Math.min(document.body.clientHeight, document.documentElement.clientHeight)
                if (height > clientHeight) {
                    height = clientHeight
                    width = (16 / 9) * height
                }

                dom.style.width = width + 'px';
                dom.style.height = height + "px";
            },
            init(url) {
                var videoElement = document.getElementById('flvjs-video');
                this.player = flvjs.createPlayer({
                    type: 'flv',
                    hasAudio: false,
                    hasVideo: true,
                    url: url
                });
                this.player.attachMediaElement(videoElement);
            },
            play: function(url) {
                if (!!url) {
                    this.init(url);
                    this.player.load();
                }
                
                this.player.play();
            },
            pause: function() {
                this.player.pause();
            },
            forward: function(rate) {
                document.getElementById('flvjs-video').playbackRate = rate;
            },
            mute: function() {
                this.player.muted = true;
            },
            cancelMute: function() {
                this.player.muted = false;
            },
            stop: function() {
                if (this.player == null)
                    return;
                
                this.player.pause();
                this.player.unload();
                this.player.detachMediaElement();
                this.player.destroy();
            }
        }
    }
</script>