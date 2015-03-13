Discover

read ./dev/upnp-spec/UPnP-arch-DeviceArchitecture-v2.0-20150220.pdf

UDP Multicast M-SEARCH

M-SEARCH * HTTP/1.1
HOST: 239.255.255.250:1900
MAN: "ssdp:discover"
MX: seconds to delay response
ST: search target
USER-AGENT: OS/version UPnP/2.0 product/version
CPFN.UPNP.ORG: friendly name of the control point
CPUUID.UPNP.ORG: uuid of the control point

possible ST

ssdp:all
    Search for all devices and services.
upnp:rootdevice
    Search for root devices only.
uuid:device-UUID
    Search for a particular device. device-UUID specified by UPnP vendor. See clause 1.1.4, “UUID format
    and recommended generation algorithms” for the MANDATORY UUID format.
urn:schemas-upnp-org:device:deviceType:ver
    Search for any device of this type where deviceType and ver are defined by the UPnP Forum working
    committee.
urn:schemas-upnp-org:service:serviceType:ver
    Search for any service of this type where serviceType and ver are defined by the UPnP Forum
    working committee.
urn:domain-name:device:deviceType:ver
    Search for any device of this typewhere domain-name (a Vendor Domain Name), deviceType and ver
    are defined by the UPnP vendor and ver specifies the highest specifies the highest supported version
    of the device type. Period characters in the Vendor Domain Name shall be replaced with hyphens in
    accordance with RFC 2141.
urn:domain-name:service:serviceType:ver
    Search for any service of this type. Where domain-name (a Vendor Domain Name), serviceType and
    ver are defined by the UPnP vendor and ver specifies the highest specifies the highest supported
    version of the service type. Period characters in the Vendor Domain Name shall be replaced with
    hyphens in accordance with RFC 2141.

parse response

HTTP/1.1 200 OK
CACHE-CONTROL: max-age = seconds until advertisement expires
DATE: when response was generated
EXT:
LOCATION: URL for UPnP description for root device
SERVER: OS/version UPnP/2.0 product/version
ST: search target
USN: composite identifier for the advertisement
BOOTID.UPNP.ORG: number increased each time device sends an initial announce or an update
message
CONFIGID.UPNP.ORG: number used for caching description information
SEARCHPORT.UPNP.ORG: number identifies port on which device responds to unicast M-SEARCH

