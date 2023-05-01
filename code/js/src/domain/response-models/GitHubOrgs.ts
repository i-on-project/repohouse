import {GitHubOrgsDto} from "../dto/GitHubOrgsDtoProperties";


export interface GitHubOrg{
    login: string,
    url: string,
    avatar_url: string
}
export interface GitHubOrgs{
    orgs: GitHubOrg[]
}

export class GitHubOrgs {
    constructor(
        dto: GitHubOrgsDto
    ) {
        const githubOrgs = dto.properties
        if(githubOrgs == null) throw new Error("GitHubOrgsDto properties is null")
        this.orgs = githubOrgs.orgs
    }
}