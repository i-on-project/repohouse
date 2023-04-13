import {SirenEntity} from "../../siren/Siren";
import {GitHubOrg} from "../response-models/GitHubOrgs";

export type GitHubOrgsDto = SirenEntity<GitHubOrgsDtoProperties>

export interface GitHubOrgsDtoProperties{
    orgs: GitHubOrg[]
}

export class GitHubOrgsDtoProperties {
    constructor(
        orgs: GitHubOrg[]
    ) {
        this.orgs = orgs
    }
}