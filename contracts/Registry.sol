pragma solidity ^0.8.20;
contract Registry
{

    struct Evidence
    {
        string ipfsHash;
        string fileHash;
        string fileType;
        bool flag;
        uint256 time;
        address uploader;
    }
    mapping(string => Evidence) private Records;
    event EvidenceLogged(string indexed fileHash, string ipfsHash, address indexed uploader, bool flag);
    function logEvidence(string memory temp_ipfsHash, string memory temp_fileHash, string memory temp_fileType, bool temp_flag)
    public
    {
        require(bytes(Records[temp_fileHash].fileHash).length == 0, "Evidence already registered");
        Records[temp_fileHash] = Evidence({ ipfsHash: temp_ipfsHash, fileHash: temp_fileHash, fileType: temp_fileType, Scan: temp_flag, time: block.timestamp, uploader: msg.sender});
        emit EvidenceLogged(temp_fileHash, temp_ipfsHash, msg.sender, temp_flag);
    }
    function Evidence(string memory _fileHashSHA512) public view returns
    (
        string memory temp_ipfsHash,
        string memory temp_fileHash,
        string memory temp_fileType,
        bool temp_flag,
        uint256 temp_time,
        address temp_uploader
    )
    {
        Evidence memory e = Records[temp_fileHash];
        require(bytes(e.fileHash).length > 0, "Record does not exist");
        return (e.ipfsHash, e.fileHash, e.fileType, e.flag, e.time, e.uploader);
    }
}